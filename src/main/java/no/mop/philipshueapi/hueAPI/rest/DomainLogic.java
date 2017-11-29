package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;
import com.philips.lighting.hue.sdk.wrapper.connection.*;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.*;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DomainLogic {

    private static final String TAG = "HueQuickStartApp";

    private static final int MAX_HUE = 65535;

    private BridgeDiscoverer bridgeDiscoverer;

    private Bridge bridge;

    private List<BridgeDiscoveryResult> bridgeDiscoveryResults;

    public static void main(String... args) throws InterruptedException {
        System.loadLibrary("huesdk");

        new DomainLogic().run();
    }

    private DomainLogic() {
        this.bridgeDiscoverer = new BridgeDiscoverer();

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
            connectToBridge(bridgeIp);
        }
        executorService.awaitTermination(13, TimeUnit.SECONDS);
        executorService.shutdown();
    }


    /**
     * Use the KnownBridges API to retrieve the last connected bridge
     * @return Ip address of the last connected bridge, or null
     */
    private String getLastUsedBridgeIp() {
        KnownBridges kb = new KnownBridges();
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
        disconnectFromBridge();

        bridgeDiscoverer.startBridgeDiscovery(bridgeDiscoveryCallback);
    }

    /**
     * Stops the bridge discovery if it is still running
     */
    private void stopBridgeDiscovery() {
        bridgeDiscoverer.stopBridgeDiscovery();
    }

    /**
     * Disconnect a bridge
     * The hue SDK supports multiple bridge connections at the same time,
     * but for the purposes of this demo we only connect to one bridge at a time.
     */
    private void disconnectFromBridge() {
        if (bridge != null) {
            bridge.disconnect();
            bridge = null;
        }
    }

    /**
     * Use the BridgeBuilder to create a bridge instance and connect to it
     */
    private void connectToBridge(String bridgeIp) {
        stopBridgeDiscovery();
        disconnectFromBridge();

        bridge = new BridgeBuilder("app name", "device name")
                .setIpAddress(bridgeIp)
                .setConnectionType(BridgeConnectionType.LOCAL)
                .setBridgeConnectionCallback(bridgeConnectionCallback)
                .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                .build();

        bridge.connect();

        System.out.println("Bridge IP: " + bridgeIp);
        System.out.println("Connecting to bridge...");
    }

    /**
     * Randomize the color of all lights in the bridge
     * The SDK contains an internal processing queue that automatically throttles
     * the rate of requests sent to the bridge, therefore it is safe to
     * perform all light operations at once, even if there are dozens of lights.
     */
    private void randomizeLights() {
        BridgeState bridgeState = bridge.getBridgeState();
        List<LightPoint> lights = bridgeState.getLights();

        Random rand = new Random();

        for (final LightPoint light : lights) {
            final LightState lightState = new LightState();

            //lightState.setHue(rand.nextInt(MAX_HUE));

            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        System.out.println("Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                    } else {
                        System.err.println("Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            System.err.println(error.toString());
                        }
                    }
                }
            });
        }
    }
    private BridgeDiscoveryCallback bridgeDiscoveryCallback = new BridgeDiscoveryCallback() {
        @Override
        public void onFinished(final List<BridgeDiscoveryResult> results, final ReturnCode returnCode) {
            // Set to null to prevent stopBridgeDiscovery from stopping it
            bridgeDiscoverer.reset();

            if (returnCode == ReturnCode.SUCCESS) {
                bridgeDiscoveryResults = results;
                System.out.println("Found " + results.size() + " bridge(s) in the network.");
                connectToBridge(bridgeDiscoveryResults.iterator().next().getIP());
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
                    randomizeLights();
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
