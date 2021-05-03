package de.maximilianheidenreich.jnet.net.server;

import de.maximilianheidenreich.jnet.net.AbstractPacketManager;
import de.maximilianheidenreich.jnet.net.Connection;
import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import de.maximilianheidenreich.jnet.packets.core.NameChangePacket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
     * Whether the server is running -> accepting connections.
     */
    private boolean running;

    /**
     * Reference to the {@link Socket} instance.
     */
    private ServerSocket serverSocket;

    /**
     * Store active connections with clients.
     */
    private final Map<String, Connection> activeConnections;

    /**
     * The ExecutorService to use for the {@link ServerThread} (accepting new connections).
     */
    @Setter
    private ExecutorService serverThreadExecutor;

    /**
     * The ExecutorService to use for any {@link Connection} threads.
     */
    @Setter
    private ExecutorService connectionThreadExecutor;


    // ======================   CONSTRUCTOR

    public Server(String host, int port) {
        super();
        this.host = host;
        this.port = port;
        this.running = false;
        this.activeConnections = new ConcurrentHashMap<>();
        this.serverThreadExecutor = ForkJoinPool.commonPool();
        this.connectionThreadExecutor = ForkJoinPool.commonPool();

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

    /**
     * Starts the server. After this, it can accept connections.
     *
     * @return Whether the server was started ({@code false} if it is already running)
     * @throws IOException
     */
    public boolean start() throws IOException {

        // RET: Already running!
        if (isRunning()) return false;

        this.serverSocket = new ServerSocket(getPort());
        getServerThreadExecutor().submit(new ServerThread(this));
        return true;
    }

    /**
     * Tells the server to stop.
     * Note: This might not instantly shut it down.
     *
     * @return Whether the server was told to stop ({@code false} if it is not running)
     */
    public boolean stop() {

        // RET: Not running!
        if (!isRunning()) return false;

        getEventLoop().stop();
        return true;
    }

    /**
     * Returns an active Connection by its name or null if not found.
     *
     * @param name
     *          The name identifying the connection
     * @return
     *          The Connection | {@code null} if not found
     */
    public Connection getConnectionByName(String name) {
        return getActiveConnections().get(name);
    }

    /**
     * Wrapper to easily send a packet to a connection by its name.
     *
     * @param packet
     *          The packet to send
     * @param connectionName
     *          The name of the target {@link Connection}
     * @return
     *          {@code true} if the packet was sent | {@code false} if not connection with that name was found
     */
    public boolean send(AbstractPacket packet, String connectionName) throws IOException {
        Connection connection = getConnectionByName(connectionName);
        if (connection == null) return false;

        connection.send(packet);
        return true;
    }

    /**
     * Wrapper to easily send a packet to a connection by its name and respond to callbacks.
     *
     * @param packet
     *          The packet to send
     * @param connectionName
     *          The name of the target {@link Connection}
     * @return
     *          The callback | {@code null} if no connection with that name was found
     */
    public CompletableFuture<AbstractPacket> sendThen(AbstractPacket packet, String connectionName) throws IOException {
        Connection connection = getConnectionByName(connectionName);
        if (connection == null) return null;

        return connection.sendThen(packet);
    }

}
