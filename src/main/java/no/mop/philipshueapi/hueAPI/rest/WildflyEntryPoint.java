package no.mop.philipshueapi.hueAPI.rest;


import com.philips.lighting.hue.sdk.PHHueSDK;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/hue")
public class WildflyEntryPoint {

	private PhilipsHueController philipsHueController;
	private PHHueSDK sdk;

	public WildflyEntryPoint() {
		sdk = PHHueSDK.create();
		philipsHueController = new PhilipsHueController(() -> sdk);
	}

	@GET
	@Produces("text/plain")
	@Consumes("text/plain")
	@Path("/light/{light}/brightness/{brightness}")
	public Response doGet(@PathParam("light") int lightIndex, @PathParam("brightness") int brightness) {
		philipsHueController.run();

		waitUntilBridgeIsSelected();
		philipsHueController.switchStateOfGivenLight(sdk.getSelectedBridge(), lightIndex, brightness);

		return Response.ok(getResponseText(lightIndex, brightness)).build();
	}

	private void waitUntilBridgeIsSelected() {
		while (sdk.getSelectedBridge() == null) {
			try {
				System.out.println("Waiting for bridgeselection");
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String getResponseText(int lightIndex, Integer newBrightness) {
		return "WildFly Swarm! The new brightness of light " + lightIndex + " is " + newBrightness;
	}
}