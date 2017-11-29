package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;

import java.util.List;
import java.util.function.Consumer;

public class BridgeDiscoveryCallbacker extends BridgeDiscoveryCallback {
    private BridgeConnectionCallback bridgeConnectionCallback;
    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback;
    private BridgeDiscoverer bridgeDiscoverer;
    private BridgeConnector bridgeConnector;
    private Consumer<Bridge> bridgeConsumer;

    BridgeDiscoveryCallbacker(BridgeConnectionCallback bridgeConnectionCallback, BridgeStateUpdatedCallback bridgeStateUpdatedCallback, BridgeDiscoverer bridgeDiscoverer, BridgeConnector bridgeConnector, Consumer<Bridge> bridgeConsumer) {
        this.bridgeConnectionCallback = bridgeConnectionCallback;
        this.bridgeStateUpdatedCallback = bridgeStateUpdatedCallback;
        this.bridgeDiscoverer = bridgeDiscoverer;
        this.bridgeConnector = bridgeConnector;
        this.bridgeConsumer = bridgeConsumer;
    }

    @Override
    public void onFinished(final List<BridgeDiscoveryResult> results, final ReturnCode returnCode) {
        // Set to null to prevent stopBridgeDiscovery from stopping it
        bridgeDiscoverer.reset();

        if (returnCode == ReturnCode.SUCCESS) {
            System.out.println("Found " + results.size() + " bridge(s) in the network.");
            bridgeConsumer.accept(bridgeConnector.connectToBridge(results.iterator().next().getIP(), bridgeConnectionCallback, bridgeStateUpdatedCallback));
        }
        else if (returnCode == ReturnCode.STOPPED) {
            System.out.println("Bridge discovery stopped.");
        }
        else {
            System.out.println("Error doing bridge discovery: " + returnCode);
        }
    }
}
