package com.yogurtpowered.dash.runnable;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.EthernetPacket;

import java.util.HashMap;
import java.util.Map;

public class ArpRunnable extends AbstractPcapRunnable {
    private static final int DISCOVERED_COUNT = 3;

    private final Map<String, Integer> arpCounters;

    public ArpRunnable(PcapHandle handle) {
        super(handle);
        this.arpCounters = new HashMap<>();
    }

    @Override
    protected void onPacket(EthernetPacket packet) {
        String sourceAddress = packet.getHeader().getSrcAddr().toString();
        Integer count = arpCounters.getOrDefault(sourceAddress, 0) + 1;

        // GLAD: 00:bb:3a:81:0d:67

        if (count == 1) {
            // do something
            System.out.println("DISCOVERED: " + sourceAddress);
        } else {
            arpCounters.put(sourceAddress, count);
        }
    }
}
