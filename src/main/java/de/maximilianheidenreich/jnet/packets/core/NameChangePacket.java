package de.maximilianheidenreich.jnet.packets.core;

import de.maximilianheidenreich.jnet.packets.AbstractPacket;
import lombok.Getter;

/**
 * Used to indicate that a connection has changed the name.
 */
@Getter
public class NameChangePacket extends AbstractPacket {

    // ======================   VARS

    /**
     * The old name.
     */
    private String oldName;

    /**
     * The new name.
     */
    private String newName;


    // ======================   CONSTRUCTOR

    public NameChangePacket(String oldName, String newName) {
        super();
        this.oldName = oldName;
        this.newName = newName;
    }

}
