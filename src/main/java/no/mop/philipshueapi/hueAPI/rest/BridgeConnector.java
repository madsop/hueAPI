package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class BridgeConnector {

    private PHHueSDK sdk;

    BridgeConnector(PHHueSDK sdk) {
        this.sdk = sdk;
    }

    void connectToLastKnownAccessPoint() {
        String username = HueProperties.getUsername();
        String lastIpAddress = HueProperties.getLastConnectedIP();

        if (username==null || lastIpAddress == null) {
            return;
        }
        createAndConnectToAccessPoint(username, lastIpAddress);
    }

    private void createAndConnectToAccessPoint(String username, String lastIpAddress) {
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        Connector.connect(sdk, accessPoint);
    }

    void findBridges() {
        PHBridgeSearchManager sm = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

}
