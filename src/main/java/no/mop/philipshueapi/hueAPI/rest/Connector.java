package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResourcesCache;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class Connector {

    void connect(SDKFacade sdk, PHAccessPoint accessPoint) {
        if (getConnectedIPAddress(sdk).isPresent()) {
            return;
        }
        sdk.connect(accessPoint);
    }

    private Optional<String> getConnectedIPAddress(SDKFacade sdk) {
        return Optional.ofNullable(sdk.getSelectedBridge())
                .map(PHBridge::getResourceCache)
                .map(PHBridgeResourcesCache::getBridgeConfiguration)
                .map(PHBridgeConfiguration::getIpAddress);
    }
}
