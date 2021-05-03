package de.maximilianheidenreich.jnet.net;

import de.maximilianheidenreich.jeventloop.EventLoop;
import de.maximilianheidenreich.jeventloop.utils.ExceptionUtils;
import de.maximilianheidenreich.jnet.events.RecvPacketEvent;
import de.maximilianheidenreich.jnet.exceptions.PacketTimeoutException;
import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import de.maximilianheidenreich.jnet.packets.ExceptionPacket;
import de.maximilianheidenreich.jnet.utils.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A wrapper class that stores registered packet handlers & callbacks.
 */
@Log4j
@Getter
public abstract class AbstractPacketManager {

    // ======================   VARS

    /**
     * A queue containing unhandled packets.
     */
    private final BlockingQueue<AbstractPacket> packetQueue;

    /**
     * All registered handlers which will be executed if an event with matching class is dequeued.
     */
    private final Map<Class<? extends AbstractPacket>, List<BiConsumer<? extends AbstractPacket, Connection>>> handlers;

    /**
     * Store all registered callbacks.
     */
    private final Map<UUID, Pair<CompletableFuture<AbstractPacket>, Long>> callbacks;   // <packetId, <callback, timeout>>

    /**
     *
     */
    private final ScheduledExecutorService scheduler;

    /**
     * A reference to an event loop that gets used for packet handling.
     */
    @Setter
    private EventLoop eventLoop;


    // ======================   CONSTRUCTOR

    /**
     * Creates a new AbstractPacketManager with a default singleThreadExecutor and new default EventLoop..
     */
    public AbstractPacketManager() {
        this(new EventLoop());
    }

    /**
     * Creates a new AbstractPacketManager with a custom executor.
     */
    public AbstractPacketManager(EventLoop eventLoop) {
        this.packetQueue = new LinkedBlockingDeque<>();
        this.handlers = new ConcurrentHashMap<>();
        this.callbacks = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.eventLoop = eventLoop;
        setupEventHandlers();
        this.eventLoop.start();

        scheduler.scheduleAtFixedRate(this::cleanupTimeoutCallbacks, 30, 80, TimeUnit.SECONDS);
    }


    // ======================   HANDLER MANAGEMENT

    /**
     * Adds a handler function which will get executed once a Packet with the matching clazz is received.
     *
     * @param clazz
     *          The class identifying the  for which the handler will be executed
     * @param handler
     *          The handler function
     */
    public <P extends AbstractPacket> void addPacketHandler(Class<P> clazz, BiConsumer<P, Connection> handler) {

        // Todo: Check null?

        if (!getHandlers().containsKey(clazz))
            getHandlers().put(clazz, new ArrayList<>());

        getHandlers().get(clazz).add(handler);
    }

    /**
     * Removed a handler.
     *
     * @param clazz
     *          The class associated the handler is associated with
     * @param handler
     *          The handler function
     * @return
     *          {@code true} if the handler was actually removed | {@code false} if no matching handler was registered
     */
    public <P extends AbstractPacket> boolean removePacketHandler(Class<P> clazz, BiConsumer<P, Connection> handler) {

        // RET: No handlers for class
        if (!getHandlers().containsKey(clazz))
            return false;

        return getHandlers().get(clazz).remove(handler);

    }


    // ======================   CALLBACK MANAGEMENT

    /**
     * Adds a callback.
     *
     * @param packet
     *          The packet associated with the callback
     * @return
     *          {@code true} if added | {@code false} if not added
     */
    public boolean addCallback(AbstractPacket packet, CompletableFuture<AbstractPacket> callback) {

        // RET: Callback already exists! This indicates a possible issue with packet id's and reusing ids to fast
        if (getCallbacks().containsKey(packet.getId()))
            return false;

        getCallbacks().put(packet.getId(), Pair.from(callback, packet.getTimout()));
        return true;

    }

    /**
     * Removed a callback.
     *
     * @param packet
     *          The packet associated with the callback
     * @return
     *          {@code true} if removed | {@code false} if not removed
     */
    public boolean removeCallback(AbstractPacket packet) {

        // RET: No registered callback!
        if (!getCallbacks().containsKey(packet.getId()))
            return false;

        getCallbacks().remove(packet.getId());
        return true;

    }

    /**
     * Removes all timed out callbacks.
     * Note: A timed out callback will automatically get removed if a timed out packet was received.
     *       But this prevents memory leaks if a packet never gets delivered!
     */
    private void cleanupTimeoutCallbacks() {
        List<UUID> timedOutPackets = getCallbacks().keySet().stream()
                .filter(uuid -> {
                    long timeout =getCallbacks().get(uuid).getB();
                    return (timeout != 0 && System.currentTimeMillis() > timeout);
                }).collect(Collectors.toList());

        log.debug("[JNet] Found " + timedOutPackets.size() + " timed out packets to remove!");
        timedOutPackets.forEach(p -> getCallbacks().remove(p));
    }


    // ======================   EVENT HANDLERS

    /**
     * Calls all registered packet handlers.
     *
     * @param event
     *          The handled event
     */
    @Synchronized
    private void handleRecvPacketEvent(RecvPacketEvent event) {
        AbstractPacket packet = event.getPacket();

        // RET: Timeout!
        if (packet.isTimeout()) {
            exceptCallback(packet, new PacketTimeoutException(packet));
            return;
        }

        // RET: No handlers for abstractEvent!
        if (!getHandlers().containsKey(event.getPacket().getClass()))
            return;

        for (BiConsumer<? extends AbstractPacket, Connection> rawHandler : getHandlers().get(packet.getClass())) {
            BiConsumer<AbstractPacket, Connection> handler = (BiConsumer<AbstractPacket, Connection>) rawHandler;

            try { handler.accept(packet, event.getConnection()); }
            catch (Exception e) {
                log.error(ExceptionUtils.getStackTraceAsString(e));
            }

        }

        if (packet instanceof ExceptionPacket) exceptCallback(packet, ((ExceptionPacket) packet).getException());
        else completeCallback(packet);

    }


    // ======================   HELPERS

    /**
     * Sets up the default event handlers.
     */
    private void setupEventHandlers() {
        getEventLoop().addEventHandler(RecvPacketEvent.class, this::handleRecvPacketEvent);
    }

    /**
     * Completes a registered callback with the specified packet as data.
     *
     * @param packet
     *          The data to pass back to the callbacks
     */
    public void completeCallback(AbstractPacket packet) {

        // RET: No registered callbacks!
        if (!getCallbacks().containsKey(packet.getId())) return;

        getCallbacks().get(packet.getId()).getA().complete(packet);
        removeCallback(packet);
    }

    /**
     * Excepts a registered callback.
     *
     * @param throwable
     *          The reason why except was called
     */
    public void exceptCallback(AbstractPacket packet, Throwable throwable) {

        // RET: No registered callbacks!
        if (!getCallbacks().containsKey(packet.getId())) return;

        getCallbacks().get(packet.getId()).getA().completeExceptionally(throwable);
        removeCallback(packet);
    }


}
