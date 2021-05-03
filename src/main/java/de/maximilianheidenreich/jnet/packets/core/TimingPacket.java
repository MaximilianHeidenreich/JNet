package de.maximilianheidenreich.jnet.packets.core;

import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import lombok.Getter;

/**
 * A simple ping packet that
 */
@Getter
public class TimingPacket extends AbstractPacket {

    // ======================   VARS

    private long timestamp;


    // ======================   CONSTRUCTOR

    public TimingPacket(long timestamp) {
        super();
        this.timestamp = timestamp;
    }


    // ======================   BUSINESS LOGIC

    /**
     * Calculates time difference (a - timestamp)
     * @param b
     * @return
     */
    public long getDiff(long a) {
        return a - timestamp;
    }


    // ======================   HELPERS

    /**
     * Generates new TimingPacket with current time as timestamp.
     * @return
     */
    public static TimingPacket now() {
        return new TimingPacket(System.currentTimeMillis());
    }

}
