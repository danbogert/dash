package com.yogurtpowered.dash;

import com.yogurtpowered.dash.runnable.ObserverRunnable;
import com.yogurtpowered.dash.utils.PcapHelper;
import org.pcap4j.core.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DashTracker implements Closeable {
    private final Map<String, DashObserver> observers;
    private final PcapNetworkInterface networkInterface;

    private PcapHandle pcapHandle;

    public DashTracker() throws PcapNativeException, IOException {
        this(null);
    }

    public DashTracker(String networkInterfaceName) throws PcapNativeException, IOException {
        this.observers = new HashMap<>();
        this.networkInterface = PcapHelper.getNetworkInterface(networkInterfaceName);

        System.out.println("Listening on " + networkInterface.getName());
        for (PcapAddress addr : networkInterface.getAddresses()) {
            if (addr.getAddress() != null) {
                System.out.println("IP address: " + addr.getAddress());
            }
        }
        System.out.println("");
    }

    public void listen() throws NotOpenException, PcapNativeException {
        if (observers.isEmpty()) {
            System.out.println("Nothing to listen for -- add a DashListener");
            return;
        }

        if (pcapHandle != null) {
            pcapHandle.close();
        }
        pcapHandle = PcapHelper.getPcapHandle(networkInterface, getBpfFilter());
        new Thread(new ObserverRunnable(pcapHandle, observers)).start();
    }

    private String getBpfFilter() {
        StringBuilder sb = new StringBuilder();
        for (String macAddress : observers.keySet()) {
            sb.append("ether host ");
            sb.append(macAddress);
            sb.append(" or ");
        }

        String pcapFilter = sb.toString().substring(0, sb.length() - 4);
        System.out.println("Filter |" + pcapFilter + "|");
        return pcapFilter;
    }

    public Dash registerDash(String name, String address) {
        System.out.println("Registering '" + address + "' with name '" + name + "'");
        return new Dash(name, address);
    }

    // TODO
//    public Dash registerDash(String name) throws NotOpenException, PcapNativeException {
//        System.out.println("Press your Dash button 3 times, wait for the LED to go out before pushing the button again");
//        try (PcapHandle handle = PcapHelper.getPcapHandle(networkInterface, "arp")) {
////            String address = getAddress((handle));
//            new ArpRunnable(handle).run();
//            return registerDash(name, null);
//        }
//    }

    public void registerListener(Dash dash, DashListener dashListener) {
        if (dash == null || dashListener == null) {
            System.out.println("Nothing to do");
            return;
        }

        DashObserver observer = observers.getOrDefault(dash.getAddress(), new DashObserver(dash, dashListener));
        observers.put(dash.getAddress(), observer);
        System.out.println("Registered listener for dash '" + dash.getName() + "'");
    }

    @Override
    public void close() {
        if (pcapHandle != null) {
            try {
                pcapHandle.setFilter("", BpfProgram.BpfCompileMode.OPTIMIZE);
            } catch (PcapNativeException | NotOpenException e) {
            }

            pcapHandle.close();
        }
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException, InterruptedException {
//        DashTracker tracker = new DashTracker("wlp58s0");
        DashTracker tracker = new DashTracker();

        Dash dash = tracker.registerDash("Glad", "00:bb:3a:81:0d:67");
        tracker.registerListener(dash, _dash -> System.out.println("ARP! " + _dash));
        tracker.listen();

        Thread.sleep(30000);

        System.out.println("Shutting down...");
        tracker.close();
    }
}