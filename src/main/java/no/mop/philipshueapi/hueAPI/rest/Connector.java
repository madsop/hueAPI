package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResourcesCache;

import java.util.Optional;

public class Connector {

    static void connect(PHHueSDK sdk, PHAccessPoint accessPoint) {
        if (getConnectedIPAddress(sdk).isPresent()) {
            return;
        }
        sdk.connect(accessPoint);
    }

    private static Optional<String> getConnectedIPAddress(PHHueSDK sdk) {
        return Optional.ofNullable(sdk.getSelectedBridge())
                .map(PHBridge::getResourceCache)
                .map(PHBridgeResourcesCache::getBridgeConfiguration)
                .map(PHBridgeConfiguration::getIpAddress);
    }
}
