package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;
import com.philips.lighting.hue.sdk.wrapper.connection.*;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DomainLogic {
    private static final int MAX_HUE = 65535;

    private BridgeDiscoverer bridgeDiscoverer;

    private BridgeConnector bridgeConnector;

    private LightController lightController;

    private Bridge bridge;

    public static void main(String[] args) throws InterruptedException {
        System.loadLibrary("huesdk");

        new DomainLogic().run();
    }

    private DomainLogic() {
        this.bridgeDiscoverer = new BridgeDiscoverer();
        this.bridgeConnector = new BridgeConnector(bridgeDiscoverer);
        this.lightController = new LightController();

        // Configure the storage location and log level for the Hue SDK
        String storageLocation = Paths.get("").toAbsolutePath().toString();
        Persistence.setStorageLocation(storageLocation, "HueQuickStart");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);
    }

    private void run() throws InterruptedException {
        // Connect to a bridge or start the bridge discovery
        String bridgeIp = getLastUsedBridgeIp();
        ExecutorService executorService = Executors.newCachedThreadPool();
        if (bridgeIp == null) {
            startBridgeDiscovery();
        }
        else {
            bridge = bridgeConnector.connectToBridge(bridgeIp, bridgeConnectionCallback, bridgeStateUpdatedCallback);
        }
        executorService.awaitTermination(13, TimeUnit.SECONDS);
        executorService.shutdown();
    }


    /**
     * Use the KnownBridges API to retrieve the last connected bridge
     * @return Ip address of the last connected bridge, or null
     */
    private String getLastUsedBridgeIp() {
        List<KnownBridge> bridges = KnownBridges.getAll();

        if (bridges.isEmpty()) {
            return null;
        }

        return Collections.max(bridges, Comparator.comparing(KnownBridge::getLastConnected)).getIpAddress();
    }

    /**
     * Start the bridge discovery search
     * Read the documentation on meethue for an explanation of the bridge discovery options
     */
    private void startBridgeDiscovery() {
        bridge = bridgeConnector.disconnectFromBridge();

        bridgeDiscoverer.startBridgeDiscovery(bridgeDiscoveryCallback);
    }

    private BridgeDiscoveryCallback bridgeDiscoveryCallback = new BridgeDiscoveryCallback() {
        @Override
        public void onFinished(final List<BridgeDiscoveryResult> results, final ReturnCode returnCode) {
            // Set to null to prevent stopBridgeDiscovery from stopping it
            bridgeDiscoverer.reset();

            if (returnCode == ReturnCode.SUCCESS) {
                System.out.println("Found " + results.size() + " bridge(s) in the network.");
                bridge = bridgeConnector.connectToBridge(results.iterator().next().getIP(), bridgeConnectionCallback, bridgeStateUpdatedCallback);
            } else if (returnCode == ReturnCode.STOPPED) {
                System.out.println("Bridge discovery stopped.");
            } else {
                System.out.println("Error doing bridge discovery: " + returnCode);
            }
        }
    };


    /**
     * The callback that receives bridge connection events
     */
    private BridgeConnectionCallback bridgeConnectionCallback = new BridgeConnectionCallback() {
        @Override
        public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
            System.out.println("Connection event: " + connectionEvent);

            switch (connectionEvent) {
                case LINK_BUTTON_NOT_PRESSED:
                    System.out.println("Press the link button to authenticate.");
                    break;

                case COULD_NOT_CONNECT:
                    System.out.println("Could not connect.");
                    break;

                case CONNECTION_LOST:
                    System.out.println("Connection lost. Attempting to reconnect.");
                    break;

                case CONNECTION_RESTORED:
                    System.out.println("Connection restored.");
                    break;

                case DISCONNECTED:
                    // User-initiated disconnection.
                    break;
                case CONNECTED:
                    lightController.randomizeLights(bridge);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> list) {
            for (HueError error : list) {
                System.err.println("Connection error: " + error.toString());
            }
        }
    };


    /**
     * The callback the receives bridge state update events
     */
    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback = new BridgeStateUpdatedCallback() {
        @Override
        public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
            System.out.println("Bridge state updated event: " + bridgeStateUpdatedEvent);

            switch (bridgeStateUpdatedEvent) {
                case INITIALIZED:
                    // The bridge state was fully initialized for the first time.
                    // It is now safe to perform operations on the bridge state.
                    System.out.println("Connected!");
                    break;

                case LIGHTS_AND_GROUPS:
                    // At least one light was updated.
                    break;

                default:
                    break;
            }
        }
    };
}
