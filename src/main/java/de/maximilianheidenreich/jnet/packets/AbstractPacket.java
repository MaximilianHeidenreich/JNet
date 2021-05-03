package de.maximilianheidenreich.jnet.packets;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        return String.format("%s-%s", this.getClass().getSimpleName(), getId().toString().split("-")[0]);
    }

}
