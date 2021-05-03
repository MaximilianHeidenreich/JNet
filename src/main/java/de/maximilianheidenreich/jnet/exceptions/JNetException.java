package de.maximilianheidenreich.jnet.exceptions;

import lombok.Getter;

/**
 * A general exception for use inside of JNet.
 */
@Getter
public class JNetException extends Exception {

    // ======================   VARS

    /**
     * A hopefully helpful message.
     */
    private final String message;


    // ======================   CONSTRUCTOR

    public JNetException(String message) {
        this.message = message;
    }

}
