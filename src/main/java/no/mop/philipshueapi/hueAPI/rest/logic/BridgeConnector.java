package no.mop.philipshueapi.hueAPI.rest.logic;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResourcesCache;
import no.mop.philipshueapi.hueAPI.rest.HueProperties;
import no.mop.philipshueapi.hueAPI.rest.Logger;
import no.mop.philipshueapi.hueAPI.rest.sdk.SDKFacade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
class BridgeConnector {

    @SuppressWarnings("unused")
    @Inject
    private SDKFacade sdk;

    @SuppressWarnings("unused")
    @Inject
    private HueProperties hueProperties;

    @SuppressWarnings("unused")
    @Inject
    private Logger logger;

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

    private void connect(PHAccessPoint accessPoint) {
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

    public void onBridgeConnected(PHBridge bridge, String username) {
        sdk.setSelectedBridge(bridge);
        sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        hueProperties.storeConnectionData(username, getLastIpAddress(bridge));
    }

    String getLastIpAddress(PHBridge bridge) {
        return bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
    }

    public void connectToArbitraryAccessPoint(List<PHAccessPoint> list) {
        list.stream()
                .peek(accessPoint -> logger.fine("Found access point " + accessPoint.getIpAddress()))
                .limit(1)
                .forEach(this::connect);
    }
}
