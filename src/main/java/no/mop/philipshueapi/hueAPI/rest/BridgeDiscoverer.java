package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;

public class BridgeDiscoverer {

    BridgeDiscovery bridgeDiscovery;

    public void startBridgeDiscovery(BridgeDiscoveryCallback bridgeDiscoveryCallback) {
        bridgeDiscovery = new BridgeDiscovery();
        bridgeDiscovery.search(BridgeDiscovery.BridgeDiscoveryOption.UPNP, bridgeDiscoveryCallback);

        System.out.println("Scanning the network for hue bridges...");
    }

    /**
     * Stops the bridge discovery if it is still running
     */
    void stopBridgeDiscovery() {
        if (bridgeDiscovery != null) {
            bridgeDiscovery.stop();
            bridgeDiscovery = null;
        }
    }

    void reset() {
        bridgeDiscovery = null;
    }
}
