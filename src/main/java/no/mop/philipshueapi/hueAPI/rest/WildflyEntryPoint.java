package no.mop.philipshueapi.hueAPI.rest;


import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.util.Optional;


@Path("/hue")
public class WildflyEntryPoint {

	@GET
	@Produces("text/plain")
	public Response doGet() {
		final Optional<Integer>[] newBrightness = new Optional[]{Optional.empty()};
		int lightIndex = 0;

		PhilipsHueController philipsHueController = new PhilipsHueController();
		Listener listener = createListener(newBrightness, lightIndex);
		philipsHueController.run(listener);

		waitForListener(newBrightness[0]);
		return Response.ok(getResponseText(lightIndex, newBrightness[0].get())).build();
	}

	private Listener createListener(Optional<Integer>[] newBrightness, int lightIndex) {
		return new Listener(PHHueSDK.getInstance()) {
			@Override
			public void onConnectionResumed(PHBridge bridge) {
				super.onConnectionResumed(bridge);
				newBrightness[0] = Optional.of(super.switchStateOfGivenLight(bridge, lightIndex));
			}
		};
	}

	private void waitForListener(Optional<Integer> newBrightness) {
		while (!newBrightness.isPresent()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String getResponseText(int lightIndex, Integer newBrightness) {
		return "Hello from WildFly Swarm! The new brightness of lamp " + lightIndex + " is " + newBrightness;
	}
}