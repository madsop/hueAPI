package no.mop.philipshueapi.hueAPI.rest.logic;

import no.mop.philipshueapi.hueAPI.rest.HueProperties;
import no.mop.philipshueapi.hueAPI.rest.sdk.NotificationManagerAdapter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@SuppressWarnings("unused")
class SetupController {

    @Inject
    private HueProperties hueProperties;

    @Inject
    private BridgeConnector bridgeConnector;

    @Inject
    private NotificationManagerAdapter notificationManagerAdapter;

    public void setup() {
        bridgeConnector.connectToLastKnownAccessPoint();
        notificationManagerAdapter.registerSDKListener();
        bridgeConnector.findBridges();
    }
}
