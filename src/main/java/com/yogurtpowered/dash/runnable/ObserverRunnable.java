package com.yogurtpowered.dash.runnable;

import com.yogurtpowered.dash.DashObserver;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.EthernetPacket;

import java.util.Map;

public class ObserverRunnable extends AbstractPcapRunnable {
    private final Map<String, DashObserver> observers;

    public ObserverRunnable(PcapHandle handle, Map<String, DashObserver> observers) {
        super(handle);
        this.observers = observers;
    }

    @Override
    protected void onPacket(EthernetPacket packet) {
        DashObserver observer = observers.get(packet.getHeader().getSrcAddr().toString());
        if (observer != null) {
            observer.observed(handle.getTimestamp());
        }
    }
}
