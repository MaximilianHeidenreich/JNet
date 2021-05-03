package de.maximilianheidenreich.jnet.net;

import de.maximilianheidenreich.jeventloop.EventLoop;
import de.maximilianheidenreich.jeventloop.utils.ExceptionUtils;
import de.maximilianheidenreich.jnet.events.RecvPacketEvent;
import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import de.maximilianheidenreich.jnet.packets.ExceptionPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

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
    private final Map<UUID, CompletableFuture<AbstractPacket>> callbacks;

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
        this(Executors.newSingleThreadExecutor(), new EventLoop());
    }

    /**
     * Creates a new AbstractPacketManager with a custom executor.
     *
     * @param packetHandlerExecutor
     *          The executor which will dequeue & handle packets.
     */
    public AbstractPacketManager(ExecutorService packetHandlerExecutor, EventLoop eventLoop) {
        this.packetQueue = new LinkedBlockingDeque<>();
        this.handlers = new ConcurrentHashMap<>();
        this.callbacks = new ConcurrentHashMap<>();
        this.eventLoop = eventLoop;
        setupEventHandlers();
        this.eventLoop.start();
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
     * @param packetId
     *          The packet id associated with the callback
     * @return
     *          {@code true} if added | {@code false} if not added
     */
    public boolean addCallback(UUID packetId, CompletableFuture<AbstractPacket> callback) {

        // RET: Callback already exists! This indicates a possible issue with packet id's and reusing ids to fast
        if (getCallbacks().containsKey(packetId))
            return false;

        getCallbacks().put(packetId, callback);
        return true;

    }

    /**
     * Removed a callback.
     *
     * @param packetId
     *          The packet id associated with the callback
     * @return
     *          {@code true} if removed | {@code false} if not removed
     */
    public boolean removeCallback(UUID packetId) {

        // RET: No registered callback!
        if (!getCallbacks().containsKey(packetId))
            return false;

        getCallbacks().remove(packetId);
        return true;

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
        final AbstractPacket packet = event.getPacket();

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

        // RET: No registered callbacks!
        if (!getCallbacks().containsKey(packet.getId())) return;

        if (packet instanceof ExceptionPacket) exceptCallback(packet.getId(), (ExceptionPacket) packet);
        else completeCallback(packet.getId(), packet);

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
    public void completeCallback(UUID packetId, AbstractPacket packet) {
        getCallbacks().get(packetId).complete(packet);
        removeCallback(packetId);
    }

    /**
     * Excepts a registered callback.
     *
     * @param exceptionPacket
     *          The reason why except was called
     */
    public void exceptCallback(UUID packetId, ExceptionPacket exceptionPacket) {
        getCallbacks().get(packetId).completeExceptionally(exceptionPacket.getException());
        removeCallback(packetId);
    }


}
