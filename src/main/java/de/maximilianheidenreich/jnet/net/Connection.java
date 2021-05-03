package de.maximilianheidenreich.jnet.net;

import de.maximilianheidenreich.jeventloop.utils.ExceptionUtils;
import de.maximilianheidenreich.jnet.events.RecvPacketEvent;
import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import de.maximilianheidenreich.jnet.packets.core.NameChangePacket;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A connection which can represent a client to server / server to client connection.
 */
@Log4j
@Getter
public class Connection implements Runnable {

    // ======================   VARS

    /**
     * A name for the connection.
     */
    private String name;

    /**
     * The parent AbstractPacketManager which will handle any packets.
     */
    private final AbstractPacketManager packetManager;

    /**
     * Reference to the {@link Socket} instance.
     */
    private final Socket socket;

    /**
     * Wrapper around the sockets {@link java.io.OutputStream}.
     */
    private final ObjectOutputStream outputStream;

    /**
     * Wrapper around the sockets {@link java.io.InputStream}.
     */
    private final ObjectInputStream inputStream;


    // ======================   CONSTRUCTOR

    public Connection(AbstractPacketManager packetManager, Socket socket, String name) throws IOException {
        this.name = name;
        this.packetManager = packetManager;
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(getSocket().getOutputStream());
        this.inputStream = new ObjectInputStream(getSocket().getInputStream());
    }

    public Connection(AbstractPacketManager packetManager, Socket socket) throws IOException {
        this(packetManager, socket, UUID.randomUUID().toString());
    }

    // ======================   BUSINESS LOGIC

    @Override
    public void run() {
        log.debug(String.format("[JNet] Started new ConnectionThread for %s", getSocket().getRemoteSocketAddress().toString()));

        Thread.currentThread().setName(
                String.format(
                        "ConnectionThread for %s |%s",
                        getSocket().getRemoteSocketAddress().toString(),
                        Thread.currentThread().getName()
                )
        );

        while (!Thread.currentThread().isInterrupted() && getPacketManager().getEventLoop().isRunning()) {
            try {
                AbstractPacket packet = recv();

                log.trace("[JNet] SOCK (" + getName() + ") Read " + packet);

                getPacketManager().getEventLoop().dispatch(new RecvPacketEvent(packet, this));
            }
            catch (IOException | ClassNotFoundException e) {
                log.error("[JNet] SOCK (" + getName() + ") Received invalid packet in " + Thread.currentThread() + "!");
                log.error(ExceptionUtils.getStackTraceAsString(e));
            }
        }

    }

    /**
     * Receives an {@link Object} and returns it as a {@link AbstractPacket}.
     *
     * @return The returned packet
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private AbstractPacket recv() throws IOException, ClassNotFoundException {
        return (AbstractPacket) getInputStream().readObject();
    }


    // ======================   SENDING PACKETS

    /**
     * Sends a {@link AbstractPacket} over the socket connection.
     *
     * @param packet
     *          The packet to send
     * @param flush
     *          Whether to flush the channel afterwards
     * @throws IOException
     */
    public void send(AbstractPacket packet, boolean flush) throws IOException {
        getOutputStream().writeObject(packet);
        if (flush)
            getOutputStream().flush();
        log.trace(String.format("[JNet] SOCK (%s) Writing %s, flush: %s", getName(), packet.toString(), flush));
    }

    /**
     * Wrapper around {@link #send(AbstractPacket, boolean)} with flush defaulting to {@code false}.
     *
     * @param packet
     *          The packet to send
     * @throws IOException
     */
    public void send(AbstractPacket packet) throws IOException {
        send(packet, false);
    }

    /**
     * Wrapper around {@link #send(AbstractPacket, boolean)} that returns callback that gets completed when
     * a packet with matching id is received.
     *
     * @param packet
     *          The packet to send
     * @return
     *          The callback
     * @throws IOException
     */
    public CompletableFuture<AbstractPacket> sendThen(AbstractPacket packet, boolean flush) throws IOException {
        CompletableFuture<AbstractPacket> future = new CompletableFuture<>();
        getPacketManager().addCallback(packet, future);
        send(packet, flush);
        return future;
    }

    /**
     * Wrapper around {@link #sendThen(AbstractPacket, boolean)} with flush defaulting to {@code false}.
     *
     * @param packet
     *          The packet to send
     * @return
     *          The callback
     * @throws IOException
     */
    public CompletableFuture<AbstractPacket> sendThen(AbstractPacket packet) throws IOException {
        return sendThen(packet, false);
    }


    // ======================   HELPERS

    /**
     * Updates the name locally.
     *
     * @param name
     *      The new name to use
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the name locally and on connected peers.
     *
     * @param name
     *          The new name to use
     * @throws IOException
     */
    public void setNameRemote(String name) throws IOException {
        String oldName = getName();
        this.name = name;

        sendThen(new NameChangePacket(oldName, name))
        .exceptionally(err -> {
            err.printStackTrace();  // TODO: fix nicer
            return null;
        });
    }

}
