package no.mop.philipshueapi.hueAPI.rest.logic;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import no.mop.philipshueapi.hueAPI.rest.HueProperties;
import no.mop.philipshueapi.hueAPI.rest.sdk.SDKFacade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped

class Listener implements PHSDKListener {

    @SuppressWarnings("unused")
    @Inject
    private SDKFacade sdk;

    @SuppressWarnings("unused")
    @Inject
    private HueProperties hueProperties;

    @SuppressWarnings("unused")
    @Inject
    private BridgeConnector bridgeConnector;

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        finePrint("Cache updated for " + getLastIpAddress(phBridge));
    }

    @Override
    public void onBridgeConnected(PHBridge bridge, String username) {
        sdk.setSelectedBridge(bridge);
        sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        hueProperties.storeConnectionData(username, getLastIpAddress(bridge));
    }

    private String getLastIpAddress(PHBridge bridge) {
        return bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        print("Authentication required on " + accessPoint);
        sdk.startPushlinkAuthentication(accessPoint);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        list.stream()
                .peek(accessPoint -> finePrint("Found access point " + accessPoint.getIpAddress()))
                .limit(1)
                .forEach(bridgeConnector::connect);
    }

    @Override
    public void onError(int i, String s) {
        System.err.println("Error: " + i + s);
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        finePrint("Connection resumed");
    }

    private void finePrint(String message) {
        // Eventually consider to introduce logger.fine here
    }

    @Override
    public void onConnectionLost(PHAccessPoint accessPoint) {
        print("Lost connection to " + accessPoint);
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        list.forEach(System.err::println);
    }

    private void print(String text) {
        System.out.println(text);
    }
}
