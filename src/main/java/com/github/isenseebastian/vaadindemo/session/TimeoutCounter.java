package com.github.isenseebastian.vaadindemo.session;

/**
 * TimeoutCounter keeps track of a timeouts current value. It can be decremented and reset to its original value.
 */
public class TimeoutCounter {

    private final int total;
    private int remaining;

    public TimeoutCounter(int timeoutTotal) {
        total = timeoutTotal;
        remaining = timeoutTotal;
    }

    public int decrement() {
        remaining -= 1;
        return remaining;
    }

    public int reset() {
        remaining = total;
        return remaining;
    }
}
