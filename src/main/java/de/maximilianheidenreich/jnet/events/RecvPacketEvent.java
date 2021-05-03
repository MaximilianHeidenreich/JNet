package de.maximilianheidenreich.jnet.events;

import de.maximilianheidenreich.jeventloop.events.AbstractEvent;
import de.maximilianheidenreich.jnet.net.Connection;
import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import lombok.Getter;

/**
 * An event that gets called whenever a new unhandled event got dequeued.
 */
@Getter
public class RecvPacketEvent extends AbstractEvent<Void> {

    // ======================   VARS

    /**
     * The packet that was received.
     */
    private final AbstractPacket packet;

    /**
     * The connection the packet was received from.
     */
    private final Connection connection;


    // ======================   CONSTRUCTOR

    public RecvPacketEvent(AbstractPacket packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    // ======================   BUSINESS LOGIC

    // ======================   HELPERS

    @Override
    public String toString() {
        return String.format("[%s-(%s)-(%d)]", getClass().getSimpleName(), getPacket(), getPriority());
    }

}
