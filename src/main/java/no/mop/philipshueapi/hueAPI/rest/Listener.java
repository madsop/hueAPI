package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Listener implements PHSDKListener {

    @Inject
    private SDKFacade sdk;

    @Inject
    private HueProperties hueProperties;

    @Inject
    private Connector connector;

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        print("Cache updated for " + phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
    }

    @Override
    public void onBridgeConnected(PHBridge bridge, String username) {
        sdk.setSelectedBridge(bridge);
        sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        String lastIpAddress =  bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
        hueProperties.storeUsername(username);
        hueProperties.storeLastIPAddress(lastIpAddress);
        hueProperties.saveProperties();
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
                .forEach(ap -> connector.connect(sdk, ap));
    }

    @Override
    public void onError(int i, String s) {
        System.err.println("Error: " + i + s);
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
