package com.yogurtpowered.dash.utils;

import org.pcap4j.core.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class PcapHelper {
    private static final int READ_TIMEOUT = 10; //ms
    private static final int SNAPLEN = 65536; // bytes
    private static final int BUFFER_SIZE = 1 * 1024 * 1024; // bytes
    private static final PrintStream SYSTEM_OUT_PRINT_STREAM = System.out;
    private static final PrintStream NOP_PRINT_STREAM = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            // nop
        }
    });

    public static PcapNetworkInterface getNetworkInterface(String networkInterfaceName) throws PcapNativeException, IOException {
        PcapNetworkInterface networkInterface = null;

        if (networkInterfaceName != null) {
            hideSystemOut();
            networkInterface = Pcaps.getDevByName(networkInterfaceName);
            showSystemOut();
        }

        if (networkInterface == null) {
            networkInterface = getNetworkInterface();
        }

        if (networkInterface == null) {
            throw new RuntimeException("Unable to determine network interface");
        }

        return networkInterface;
    }

    public static PcapNetworkInterface getNetworkInterface() throws PcapNativeException {
        hideSystemOut();
        final List<PcapNetworkInterface> networkInterfaces = Pcaps.findAllDevs();
        showSystemOut();
        for (int i = 0; i < networkInterfaces.size(); i++) {
            System.out.println(i + ") " + networkInterfaces.get(i));
        }

        try (Scanner scanner = new Scanner(System.in)) {
            int index = InputHelper.getInt(scanner, "Which network interface should be monitored? ", 0, networkInterfaces.size() - 1);
            return networkInterfaces.get(index);
        }
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

    private static void hideSystemOut() {
        System.setOut(NOP_PRINT_STREAM);
    }

    private static void showSystemOut() {
        System.setOut(SYSTEM_OUT_PRINT_STREAM);
    }
}
