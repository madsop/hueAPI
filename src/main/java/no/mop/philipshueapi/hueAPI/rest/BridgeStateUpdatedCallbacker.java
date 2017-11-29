package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;


/**
 * The callback the receives bridge state update events
 */
class BridgeStateUpdatedCallbacker extends BridgeStateUpdatedCallback {
    @Override
    public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
        System.out.println("Bridge state updated event: " + bridgeStateUpdatedEvent);

        switch (bridgeStateUpdatedEvent) {
            case INITIALIZED:
                // The bridge state was fully initialized for the first time.
                // It is now safe to perform operations on the bridge state.
                System.out.println("Connected!");
                break;

            case LIGHTS_AND_GROUPS:
                // At least one light was updated.
                break;

            default:
                break;
        }
    }
}
