package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BridgeConnector {

    @Inject
    private SDKFacade sdk;

    @Inject
    private HueProperties hueProperties;

    @Inject
    private Connector connector;

    void connectToLastKnownAccessPoint() {
        String username = hueProperties.getUsername();
        String lastIpAddress = hueProperties.getLastConnectedIP();

        if (username==null || lastIpAddress == null) {
            return;
        }
        createAndConnectToAccessPoint(username, lastIpAddress);
    }

    private void createAndConnectToAccessPoint(String username, String lastIpAddress) {
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        connector.connect(sdk, accessPoint);
    }

    void findBridges() {
        PHBridgeSearchManager sm = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

}
