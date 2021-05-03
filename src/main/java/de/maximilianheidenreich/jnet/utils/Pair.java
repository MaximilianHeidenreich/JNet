package de.maximilianheidenreich.jnet.utils;

import lombok.Getter;

/**
 * A very basic Pair implementation.
 *
 * @param <A>
 *              The type of the first value
 * @param <B>
 *              The type of the second value
 */
public class Pair<A, B> {

    // ======================   VARS

    /**
     * The stored values.
     */
    @Getter
    private final A a;
    @Getter
    private final B b;


    // ======================   CONSTRUCTOR

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }


    // ======================   HELPERS

    public static <A, B> Pair<A, B> from(A a, B b) {
        return new Pair<>(a, b);
    }

}

