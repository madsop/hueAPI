package no.mop.philipshueapi.hueAPI.rest.logic;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import no.mop.philipshueapi.hueAPI.rest.HueProperties;
import no.mop.philipshueapi.hueAPI.rest.sdk.SDKFacade;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@SuppressWarnings("unused")
public class PhilipsHueController {

    @Inject
    private SDKFacade sdk;

    @Inject
    private BridgeConnector bridgeConnector;

    @Inject
    private HueProperties hueProperties;

    @Inject
    private Listener listener;

    @PreDestroy
    public void tearDown() {
        sdk.getNotificationManager().unregisterSDKListener(listener);
    }

    public void run() {
        bridgeConnector.connectToLastKnownAccessPoint();
        sdk.getNotificationManager().registerSDKListener(listener);
        bridgeConnector.findBridges();
    }

    public void switchStateOfGivenLight(PHBridge bridge, int lightIndex, int brightness) {
        PHLight light = getGivenLight(bridge, lightIndex);
        PHLightState lastKnownLightState = light.getLastKnownLightState();
        if (!lastKnownLightState.isReachable()) {
            System.err.println("Light " + lightIndex + " is not reachable.");
            return;
        }
        System.out.println("New brightness: " + brightness);
        lastKnownLightState.setBrightness(brightness);
        //bridge.updateLightState(light, lastKnownLightState);
    }

    PHLight getGivenLight(PHBridge bridge, int lightIndex) {
        return bridge.getResourceCache().getAllLights().get(lightIndex);
    }

    public int getNumberOfLights() {
        return sdk.getSelectedBridge().getResourceCache().getAllLights().size();
    }
}
