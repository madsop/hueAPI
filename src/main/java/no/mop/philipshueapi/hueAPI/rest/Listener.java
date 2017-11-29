package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;

public class Listener implements PHSDKListener {
    private PHHueSDK sdk;
    public Listener(PHHueSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {

    }

    @Override
    public void onBridgeConnected(PHBridge bridge, String username) {
        sdk.setSelectedBridge(bridge);
        sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        String lastIpAddress =  bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
        HueProperties.storeUsername(username);
        HueProperties.storeLastIPAddress(lastIpAddress);
        HueProperties.saveProperties();

        switchStateOfFirstLight(bridge);
    }

    private void switchStateOfFirstLight(PHBridge bridge) {
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        allLights.forEach(System.out::println);
        PHLight light = allLights.get(0);
        PHLightState lastKnownLightState = light.getLastKnownLightState();
        lastKnownLightState.setOn(!lastKnownLightState.isOn());
        //bridge.updateLightState(light, lastKnownLightState);
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        print("Authentication required on " + accessPoint);
        sdk.startPushlinkAuthentication(accessPoint);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        if (sdk.getSelectedBridge().getResourceCache().getBridgeConfiguration().getIpAddress() != null) {
            return;
        }
        list.stream()
                .peek(accessPoint -> System.out.println(accessPoint.getIpAddress()))
                .limit(1)
                .forEach(sdk::connect);
    }

    @Override
    public void onError(int i, String s) {
        System.err.println(i + s);
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        print("Connection resumed");
    }

    private void print(String text) {
        System.out.println(text);
    }

    @Override
    public void onConnectionLost(PHAccessPoint accessPoint) {
        print("Lost connection to " + accessPoint);
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        list.forEach(System.err::println);
    }
}
