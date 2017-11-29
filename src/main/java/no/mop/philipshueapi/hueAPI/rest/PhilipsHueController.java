package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PhilipsHueController {
    private static final int MAX_HUE = 65535;

    private BridgeDiscoverer bridgeDiscoverer;
    private BridgeConnector bridgeConnector;
    private LightController lightController;

    private BridgeConnectionCallback bridgeConnectionCallback;
    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback;

    private BridgeDiscoveryCallback bridgeDiscoveryCallback;

    private Bridge bridge;
    private PHHueSDK sdk;

    PhilipsHueController() {
        sdk = PHHueSDK.create();
        HueProperties.loadProperties();  // Load in HueProperties, if first time use a properties file is created.
        connectToLastKnownAccessPoint();
        sdk.getNotificationManager().registerSDKListener(new Listener(sdk));
        findBridges();

        /*this.bridgeDiscoverer = new BridgeDiscoverer();
        this.bridgeConnector = new BridgeConnector(bridgeDiscoverer);
        this.lightController = new LightController();
        this.bridgeConnectionCallback = new BridgeConnectionCallbacker(() -> lightController.randomizeLights(bridge));
        this.bridgeStateUpdatedCallback = new BridgeStateUpdatedCallbacker();
        this.bridgeDiscoveryCallback = new BridgeDiscoveryCallbacker(bridgeConnectionCallback, bridgeStateUpdatedCallback, bridgeDiscoverer, bridgeConnector, bridge -> this.bridge = bridge);

        // Configure the storage location and log level for the Hue SDK
        String storageLocation = Paths.get("").toAbsolutePath().toString();
        Persistence.setStorageLocation(storageLocation, "HueQuickStart");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO); */
    }

    public void findBridges() {
        sdk = PHHueSDK.getInstance();
        PHBridgeSearchManager sm = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    public boolean connectToLastKnownAccessPoint() {
        String username = HueProperties.getUsername();
        String lastIpAddress =  HueProperties.getLastConnectedIP();

        if (username==null || lastIpAddress == null) {
            return false;
        }
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        sdk.connect(accessPoint);
        return true;
    }


    void run() throws InterruptedException {
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

}
