package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;

import java.util.List;

/**
 * The callback that receives bridge connection events
 */
public class BridgeConnectionCallbacker extends BridgeConnectionCallback {

    private Runnable onConnected;

    BridgeConnectionCallbacker(Runnable onConnected) {
        this.onConnected = onConnected;
    }

    @Override
    public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
        System.out.println("Connection event: " + connectionEvent);

        switch (connectionEvent) {
            case LINK_BUTTON_NOT_PRESSED:
                System.out.println("Press the link button to authenticate.");
                break;

            case COULD_NOT_CONNECT:
                System.out.println("Could not connect.");
                break;

            case CONNECTION_LOST:
                System.out.println("Connection lost. Attempting to reconnect.");
                break;

            case CONNECTION_RESTORED:
                System.out.println("Connection restored.");
                break;

            case DISCONNECTED:
                // User-initiated disconnection.
                break;
            case CONNECTED:
                onConnected.run();
                break;
            default:
                break;
        }

    }

    @Override
    public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> list) {
        for (HueError error : list) {
            System.err.println("Connection error: " + error.toString());
        }
    }
}
