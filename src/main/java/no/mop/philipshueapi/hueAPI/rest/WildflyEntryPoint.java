package no.mop.philipshueapi.hueAPI.rest;


import no.mop.philipshueapi.hueAPI.rest.logic.PhilipsHueController;
import no.mop.philipshueapi.hueAPI.rest.sdk.SDKFacade;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/hue")
public class WildflyEntryPoint {

	@SuppressWarnings("unused")
	@Inject
	private PhilipsHueController philipsHueController;

	@SuppressWarnings("unused")
	@Inject
	private SDKFacade sdk;

	@GET
	@Produces("text/plain")
	@Consumes("text/plain")
	@Path("/light/{light}/brightness/{brightness}")
	public Response switchStateOfLight(@PathParam("light") int lightIndex, @PathParam("brightness") int brightness) {
		philipsHueController.run();

		waitUntilBridgeIsSelected();
		try {
			philipsHueController.switchStateOfGivenLight(sdk.getSelectedBridge(), lightIndex, brightness);

			return Response.ok(getResponseText(lightIndex, brightness)).build();
		}
		catch (HueAPIException e) {
			logException(e);
			return Response.ok(e.getMessage()).build();
		}
	}

	private void logException(HueAPIException e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
	}

	@GET
	@Produces("text/plain")
	@Consumes("text/plain")
	@Path("/lights")
	public Response getNumberOfLights() {
		philipsHueController.run();
		waitUntilBridgeIsSelected();

		String responseText = philipsHueController.getNumberOfLights() + "";
		return Response.ok(responseText).build();
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