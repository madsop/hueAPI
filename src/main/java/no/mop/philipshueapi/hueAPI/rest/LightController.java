package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;

import java.util.List;
import java.util.Random;

public class LightController {

    /**
     * Randomize the color of all lights in the bridge
     * The SDK contains an internal processing queue that automatically throttles
     * the rate of requests sent to the bridge, therefore it is safe to
     * perform all light operations at once, even if there are dozens of lights.
     */
    void randomizeLights(Bridge bridge) {
        BridgeState bridgeState = bridge.getBridgeState();
        List<LightPoint> lights = bridgeState.getLights();

        Random rand = new Random();

        for (final LightPoint light : lights) {
            final LightState lightState = new LightState();

            //lightState.setHue(rand.nextInt(MAX_HUE));

            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        System.out.println("Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                    } else {
                        System.err.println("Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            System.err.println(error.toString());
                        }
                    }
                }
            });
        }
    }
}
