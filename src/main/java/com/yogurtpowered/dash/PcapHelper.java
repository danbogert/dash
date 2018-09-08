package com.yogurtpowered.dash;

import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;

import java.io.IOException;

public class PcapHelper {
    private static final int READ_TIMEOUT = 10; //ms
    private static final int SNAPLEN = 65536; // bytes
    private static final int BUFFER_SIZE = 1 * 1024 * 1024; // bytes

    public static PcapNetworkInterface getNetworkInterface(String networkInterfaceName) throws PcapNativeException, IOException {
        PcapNetworkInterface networkInterface = null;

        if (networkInterfaceName != null) {
            networkInterface = Pcaps.getDevByName(networkInterfaceName);
        }

        if (networkInterface == null) {
            networkInterface = new NifSelector().selectNetworkInterface();
        }

        if (networkInterface == null) {
            throw new RuntimeException("Unable to determine network interface");
        }

        return networkInterface;
    }

    public static PcapHandle getPcapHandle(PcapNetworkInterface networkInterface, String filter) throws PcapNativeException, NotOpenException {
        PcapHandle handle = new PcapHandle.Builder(networkInterface.getName())
                .snaplen(SNAPLEN)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .timeoutMillis(READ_TIMEOUT)
                .bufferSize(BUFFER_SIZE)
                .build();

        if (filter != null && !filter.trim().isEmpty()) {
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }

        return handle;
    }
}
