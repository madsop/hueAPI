package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;

public class PhilipsHueController {
    private static final int MAX_HUE = 65535;

    private PHHueSDK sdk;
    private BridgeConnector bridgeConnector;

    PhilipsHueController() {
        sdk = PHHueSDK.create();
        HueProperties.loadProperties();  // Load in HueProperties, if first time use a properties file is created.
        this.bridgeConnector = new BridgeConnector(sdk);
    }

    void run() {
        run(new Listener(sdk));
    }

    void run(PHSDKListener listener) {
        bridgeConnector.connectToLastKnownAccessPoint();
        sdk.getNotificationManager().registerSDKListener(listener);
        bridgeConnector.findBridges();
    }
}
