package de.maximilianheidenreich.jnet.net.server;

import de.maximilianheidenreich.jeventloop.utils.ExceptionUtils;
import de.maximilianheidenreich.jnet.events.RecvPacketEvent;
import de.maximilianheidenreich.jnet.net.Connection;
import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

/**
 * A server thread that handles new connections.
 */
@Log4j
public class ServerThread implements Runnable {

    // ======================   VARS

    /**
     * A reference to the parent server instance.
     */
    @Getter
    private final Server server;


    // ======================   CONSTRUCTOR

    public ServerThread(Server server) {
        this.server = server;
    }


    // ======================   BUSINESS LOGIC

    @Override
    public void run() {
        log.debug(String.format("Started new ServerThread for %s", getServer().getServerSocket().getLocalSocketAddress().toString()));

        Thread.currentThread().setName(
                String.format(
                        "ServerThread for %s |%s",
                        getServer().getServerSocket().getLocalSocketAddress().toString(),
                        Thread.currentThread().getName()
                )
        );

        while (!Thread.currentThread().isInterrupted() && getServer().getEventLoop().isRunning()) {
            try {
                Socket acceptedSocket = getServer().getServerSocket().accept();

                log.debug(String.format("Accepted connection from %s", acceptedSocket.getRemoteSocketAddress().toString()));

                Connection connection = new Connection(getServer(), acceptedSocket);

                getServer().getActiveConnections().put(acceptedSocket.getRemoteSocketAddress().toString(), connection);

                ForkJoinPool.commonPool().submit(connection);
            }
            catch (IOException e) {
               log.error(ExceptionUtils.getStackTraceAsString(e));
            }
        }
    }


    // ======================   HELPERS


}
