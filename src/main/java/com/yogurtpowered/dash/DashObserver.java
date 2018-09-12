package com.yogurtpowered.dash;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class DashObserver {
    private static final long LOCKED_FOR_SECS = 30;

    private final Dash dash;
    private final Set<DashListener> listeners;

    private Timestamp lockedUntilTimestamp;

    public DashObserver(Dash dash, DashListener listener) {
        this.dash = dash;
        this.listeners = new HashSet<>();
        this.listeners.add(listener);
        this.lockedUntilTimestamp = new Timestamp(0);
    }

    public void observed(Timestamp timestamp) {
        if (timestamp.compareTo(lockedUntilTimestamp) > 0) {
            lockedUntilTimestamp = Timestamp.from(timestamp.toInstant().plusSeconds(LOCKED_FOR_SECS));

            for (DashListener listener : listeners) {
                listener.onButtonPressed(dash);
            }
        }
    }
}
