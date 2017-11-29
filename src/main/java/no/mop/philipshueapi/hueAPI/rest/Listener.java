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

    Listener(PHHueSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        print("Cache updated for " + phBridge);
        list.stream().map(Object::toString).forEach(this::print);
    }

    @Override
    public void onBridgeConnected(PHBridge bridge, String username) {
        sdk.setSelectedBridge(bridge);
        sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        String lastIpAddress =  bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
        HueProperties.storeUsername(username);
        HueProperties.storeLastIPAddress(lastIpAddress);
        HueProperties.saveProperties();
    }

    int switchStateOfGivenLight(PHBridge bridge, int lightIndex) {
        PHLight light = getGivenLight(bridge, lightIndex);
        PHLightState lastKnownLightState = light.getLastKnownLightState();
        int brightness = getRandomBrightness();
        System.out.println("New brightness: " + brightness);
        lastKnownLightState.setBrightness(brightness);
        bridge.updateLightState(light, lastKnownLightState);
        return brightness;
    }

    private PHLight getGivenLight(PHBridge bridge, int lightIndex) {
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        return allLights.get(lightIndex);
    }

    private int getRandomBrightness() {
        return (int) (Math.random()*254);
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        print("Authentication required on " + accessPoint);
        sdk.startPushlinkAuthentication(accessPoint);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        list.stream()
                .peek(accessPoint -> print("Found access point " + accessPoint.getIpAddress()))
                .limit(1)
                .forEach(ap -> Connector.connect(sdk, ap));
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
