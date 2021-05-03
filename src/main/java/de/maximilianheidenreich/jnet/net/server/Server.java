package de.maximilianheidenreich.jnet.net.server;

import de.maximilianheidenreich.jnet.net.AbstractPacketManager;
import de.maximilianheidenreich.jnet.net.Connection;
import de.maximilianheidenreich.jnet.packets.core.NameChangePacket;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

/**
 * A server which can accept multiple concurrent connections.
 */
@Log4j
@Getter
public class Server extends AbstractPacketManager {

    // ======================   VARS

    /**
     * The host address the server socket listens on.
     */
    private final String host;

    /**
     * The port the server socket listens on.
     */
    private final int port;

    /**
     * Reference to the {@link Socket} instance.
     */
    private ServerSocket serverSocket;

    /**
     * Store active connections with clients.
     */
    private final Map<String, Connection> activeConnections;


    // ======================   CONSTRUCTOR

    public Server(String host, int port) {
        super();
        this.host = host;
        this.port = port;
        this.activeConnections = new ConcurrentHashMap<>();

        addPacketHandler(NameChangePacket.class, (p, conn) -> {

            getActiveConnections().remove(p.getOldName(), conn);
            conn.setName(p.getNewName());
            getActiveConnections().put(p.getNewName(), conn);

        });
    }
    public Server(int port) {
        this("0.0.0.0", port);
    }


    // ======================   BUSINESS LOGIC

    // ======================   HELPERS

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(getPort());
        ForkJoinPool.commonPool().submit(new ServerThread(this));
    }

    public void stop() {}

    /*public CompletableFuture<AbstractCallbackPacket> send(AbstractCallbackPacket packet, Connection connection) throws IOException {

        CompletableFuture<AbstractCallbackPacket> future = new CompletableFuture<>();

        //getPacketsAwaitingResponse().add(packet);
        //packet.getCallbacks().add(future);

        // Send to channel.
        connection.send(packet);

        return future;

    }*/

}
