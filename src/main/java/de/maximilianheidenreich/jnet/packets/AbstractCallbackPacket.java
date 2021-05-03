package de.maximilianheidenreich.jnet.packets;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A abstract network request packet that can store callbacks.
 *
 */
@Getter
public class AbstractCallbackPacket extends AbstractPacket {

    // ======================   VARS

    /**
     * Set to true when the packet was received.
     * This prevents calling of complete() and except()!
     */
    @Setter
    private boolean received = false;

    // ======================   CONSTRUCTOR

    /**
     * Create a new AbstractPacket with a random id based on the class name.
     */
    public AbstractCallbackPacket() {
        super();
        //this.callbacks = new HashSet<>();
    }

    /**
     * Creates a new AbstractPacket with a specified id.
     *
     * @param id
     *          The id to use.
     */
    public AbstractCallbackPacket(UUID id) {
        super(id);
        //this.callbacks = new HashSet<>();
    }


    // ======================   CALLBACK MANAGEMENT

    /**
     * Adds a callback which can be completed or excepted using the respective methods.
     *
     * @param callback
     *          The callback itself
     */
    @Synchronized
    public void addCallback(CompletableFuture<AbstractCallbackPacket> callback) {
        //getCallbacks().add(callback);
    }

    /**
     * Removes a callback specified by id.
     *
     * @param callback
     *          The callback which will be removed
     * @return
     *          {@code true} if the callback was actually removed, {@code false} if not
     */
    @Synchronized
    public boolean removeCallback(CompletableFuture<AbstractCallbackPacket> callback) {
        //return getCallbacks().remove(callback);
        return true;
    }

    // ======================   HELPERS

    /**
     * Completes all registered callbacks with the specified packet as data..
     *
     * @param packet
     *          The data to pass back to the callbacks
     */
    public void complete(AbstractCallbackPacket packet) {
        //getCallbacks().forEach(c -> c.complete(packet));
    }

    /**
     * Completes all registered callbacks without this packet as data..
     */
    public void complete() {
        //getCallbacks().forEach(c -> c.complete(this));
    }

    /**
     * Excepts all registered callbacks.
     *
     * @param throwable
     *          The reason why except was called
     */
    public void except(Throwable throwable) {
        //getCallbacks().forEach(c -> c.completeExceptionally(throwable));
    }

}
