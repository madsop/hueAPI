package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PhilipsHueController {

    @Inject
    private SDKFacade sdk;

    @Inject
    private BridgeConnector bridgeConnector;

    @Inject
    private HueProperties hueProperties;

    @Inject
    private Listener listener;

    @PostConstruct
    public void setUp() {
        hueProperties.loadProperties();
    }

    void run() {
        bridgeConnector.connectToLastKnownAccessPoint();
        sdk.getNotificationManager().registerSDKListener(listener);
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
        return bridge.getResourceCache().getAllLights().get(lightIndex);
    }
}
