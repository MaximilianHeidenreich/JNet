package de.maximilianheidenreich.jnet.exceptions;

import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import lombok.Getter;

/**
 * Thrown when a packed was dequeued but it timed out.
 */
@Getter
public class PacketTimeoutException extends JNetException {

    // ======================   VARS

    /**
     * The packet that timed out.
     */
    private final AbstractPacket packet;


    // ======================   CONSTRUCTOR

    public PacketTimeoutException(AbstractPacket packet) {
        super("Packet " + packet + " timed out at " + System.currentTimeMillis());
        this.packet = packet;
    }

}
