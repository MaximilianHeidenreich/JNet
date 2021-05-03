package de.maximilianheidenreich.jnet.net.client;

import de.maximilianheidenreich.jnet.net.AbstractPacketManager;
import de.maximilianheidenreich.jnet.net.Connection;
import de.maximilianheidenreich.jnet.packets.core.NameChangePacket;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

/**
 * A client that cann connect and communicate with a server.
 */
@Log4j
@Getter
public class Client extends AbstractPacketManager {

    // ======================   VARS

    /**
     * The host address the client is connected to.
     */
    private String host;

    /**
     * The port the client socket is connected to.
     */
    private int port;

    /**
     * The connection to a server once established.
     */
    private Connection connection;


    // ======================   CONSTRUCTOR

    public Client() {}


    // ======================   BUSINESS LOGIC

    /**
     * Connect to a server.
     *
     * @param host
     *          The remote host
     * @param port
     *          The remote port
     * @throws IOException
     */
    public void connect(String host, int port) throws IOException {
        this.connection = new Connection(this, new Socket(host, port));
        this.host = host;
        this.port = port;
        ForkJoinPool.commonPool().submit(this.connection);
    }

    // ======================   HELPERS

    /**
     * Updates the name locally.
     *
     * @param name
     *      The new name to use
     */
    public void setName(String name) {
       getConnection().setName(name);
    }

    /**
     * Updates the name locally and on connected peers.
     *
     * @param name
     *          The new name to use
     * @throws IOException
     */
    public void setNameRemote(String name) throws IOException {
        getConnection().setNameRemote(name);
    }


}
