package com.yogurtpowered.dash.runnable;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;

public abstract class AbstractPcapRunnable implements Runnable {
    protected final PcapHandle handle;

    public AbstractPcapRunnable(PcapHandle handle) {
        this.handle = handle;
    }

    @Override
    public void run() {
        while (true) {
            Packet packet;
            try {
                packet = handle.getNextPacket();
            } catch (NotOpenException e) {
                break;
            }

            if (packet == null) {
                continue;
            }

            if (packet instanceof EthernetPacket) {
                EthernetPacket ethernetPacket = (EthernetPacket) packet;
                onPacket(ethernetPacket);
            }
        }
    }

    protected abstract void onPacket(EthernetPacket packet);
}
