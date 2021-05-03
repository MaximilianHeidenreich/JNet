package de.maximilianheidenreich.jnet.packets;

import lombok.Getter;

import java.util.UUID;

/**
 * A packet that indicates some exception.
 */
@Getter
public class ExceptionPacket extends AbstractPacket {

    // ======================   VARS

    /**
     * The exception.
     */
    private final Exception exception;


    // ======================   CONSTRUCTOR

    public ExceptionPacket(UUID id, Exception exception) {
        super(id);
        this.exception = exception;
    }

    public ExceptionPacket(Exception exception) {
        super();
        this.exception = exception;
    }


    // ======================   HELPERS

    /**
     * Creates a new ExceptionPacket from another packet.
     *
     * @param packet
     *          The original packet
     * @param exception
     *          The exception stored inside the new packet
     * @return
     *          The generated packet
     */
    public static ExceptionPacket fromPacket(AbstractPacket packet, Exception exception) {
        return new ExceptionPacket(packet.getId(), exception);
    }

}
