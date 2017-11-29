package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;

public class BridgeConnector {

    private Bridge bridge;
    private final BridgeDiscoverer bridgeDiscoverer;

    BridgeConnector(BridgeDiscoverer bridgeDiscoverer) {
        this.bridgeDiscoverer = bridgeDiscoverer;
    }

    /**
     * Disconnect a bridge
     * The hue SDK supports multiple bridge connections at the same time,
     * but for the purposes of this demo we only connect to one bridge at a time.
     */
    Bridge disconnectFromBridge() {
        if (bridge != null) {
            bridge.disconnect();
            bridge = null;
        }
        return null;
    }

    /**
     * Use the BridgeBuilder to create a bridge instance and connect to it
     */
    Bridge connectToBridge(String bridgeIp, BridgeConnectionCallback bridgeConnectionCallback, BridgeStateUpdatedCallback bridgeStateUpdatedCallback) {
        bridgeDiscoverer.stopBridgeDiscovery();
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
        return bridge;
    }
}
