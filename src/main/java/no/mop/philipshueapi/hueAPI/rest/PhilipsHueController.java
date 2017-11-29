package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class PhilipsHueController {
    private static final int MAX_HUE = 65535;

    private PHHueSDK sdk;
    private Listener listener;

    PhilipsHueController() {
        sdk = PHHueSDK.create();
        HueProperties.loadProperties();  // Load in HueProperties, if first time use a properties file is created.
    }

    void run() {
        connectToLastKnownAccessPoint();
        listener = new Listener(sdk);
        sdk.getNotificationManager().registerSDKListener(listener);
        findBridges();
    }

    private void findBridges() {
        sdk = PHHueSDK.getInstance();
        PHBridgeSearchManager sm = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    private void connectToLastKnownAccessPoint() {
        String username = HueProperties.getUsername();
        String lastIpAddress =  HueProperties.getLastConnectedIP();

        if (username==null || lastIpAddress == null) {
            return;
        }
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        sdk.connect(accessPoint);
    }
}
