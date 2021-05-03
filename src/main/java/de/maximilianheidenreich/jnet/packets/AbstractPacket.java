package de.maximilianheidenreich.jnet.packets;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A abstract network packet.
 */
@Getter
public class AbstractPacket implements Serializable {

    // ======================   VARS

    /**
     * A unique id identifying the packet.
     */
    @Setter
    private UUID id;

    /**
     * The timestamp after which the packet will get dropped by the handler and no callbacks will be executed.
     * Note: 0 = NEVER
     */
    @Getter
    private long timout = 0;


    // ======================   CONSTRUCTOR

    /**
     * Create a new AbstractPacket with a random id.
     */
    public AbstractPacket() {
        this.id = UUID.randomUUID();
    }

    /**
     * Creates a new AbstractPacket with a specified id.
     *
     * @param id
     *          The id to use.
     */
    public AbstractPacket(UUID id) {
        this.id = id;
    }


    // ======================   HELPERS

    @Override
    public String toString() {
        return String.format("%s(%s)-t%d", this.getClass().getSimpleName(), getId().toString().split("-")[0], getTimout());
    }

    /**
     * Sets the timeout value to now + the specified time.
     *
     * @param time
     *          The time amount value
     * @param unit
     *          The {@link TimeUnit} of time
     */
    public void setTimout(int time, TimeUnit unit) {
        this.timout = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit);
    }

    /**
     * Can be used to quickly check if a packet has timed out.
     *
     * @return
     *          {@code true} if current time > timout | {@code false} if not
     */
    public boolean isTimeout() {
        return (this.timout != 0 && System.currentTimeMillis() > this.timout);
    }

}
