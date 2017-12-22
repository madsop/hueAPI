package no.mop.philipshueapi.hueAPI.rest.logic;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import no.mop.philipshueapi.hueAPI.rest.HueProperties;
import no.mop.philipshueapi.hueAPI.rest.Logger;
import no.mop.philipshueapi.hueAPI.rest.sdk.SDKFacade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Listener implements PHSDKListener {

    @SuppressWarnings("unused")
    @Inject
    private SDKFacade sdk;

    @SuppressWarnings("unused")
    @Inject
    private HueProperties hueProperties;

    @SuppressWarnings("unused")
    @Inject
    private BridgeConnector bridgeConnector;

    @SuppressWarnings("unused")
    @Inject
    private Logger logger;

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        logger.fine("Cache updated for " + bridgeConnector.getLastIpAddress(phBridge));
    }

    @Override
    public void onBridgeConnected(PHBridge bridge, String username) {
        bridgeConnector.onBridgeConnected(bridge, username);
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        logger.warn("Authentication required on " + accessPoint);
        sdk.startPushlinkAuthentication(accessPoint);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        bridgeConnector.connectToArbitraryAccessPoint(list);
    }

    @Override
    public void onError(int i, String s) {
        System.err.println("Error: " + i + s);
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        logger.fine("Connection resumed");
    }

    @Override
    public void onConnectionLost(PHAccessPoint accessPoint) {
        logger.warn("Lost connection to " + accessPoint);
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        logger.logParsingErrors(list);
    }
}
