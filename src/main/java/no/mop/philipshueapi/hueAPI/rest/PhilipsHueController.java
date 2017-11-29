package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;

public class PhilipsHueController {
    private PHHueSDK sdk;
    private BridgeConnector bridgeConnector;

    PhilipsHueController() {
        sdk = PHHueSDK.create();
        HueProperties.loadProperties();
        this.bridgeConnector = new BridgeConnector(sdk);
    }

    void run() {
        bridgeConnector.connectToLastKnownAccessPoint();
        sdk.getNotificationManager().registerSDKListener(new Listener(sdk));
        bridgeConnector.findBridges();
    }

    void switchStateOfGivenLight(PHBridge bridge, int lightIndex, int brightness) {
        PHLight light = getGivenLight(bridge, lightIndex);
        PHLightState lastKnownLightState = light.getLastKnownLightState();
        System.out.println("New brightness: " + brightness);
        lastKnownLightState.setBrightness(brightness);
        //bridge.updateLightState(light, lastKnownLightState);
    }

    private PHLight getGivenLight(PHBridge bridge, int lightIndex) {
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        return allLights.get(lightIndex);
    }
}
