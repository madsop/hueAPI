package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResourcesCache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class BridgeConnector {

    @Inject
    private SDKFacade sdk;

    @Inject
    private HueProperties hueProperties;

    public void connectToLastKnownAccessPoint() {
        Optional<String> username = Optional.ofNullable(hueProperties.getUsername());
        Optional<String> lastIpAddress = Optional.ofNullable(hueProperties.getLastConnectedIP());

        if (!username.isPresent() || !lastIpAddress.isPresent()) {
            return;
        }

        createAndConnectToAccessPoint(username.get(), lastIpAddress.get());
    }

    private void createAndConnectToAccessPoint(String username, String lastIpAddress) {
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        connect(accessPoint);
    }

    public void findBridges() {
        PHBridgeSearchManager searchManager = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        searchManager.search(true, true);
    }

    public void connect(PHAccessPoint accessPoint) {
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
