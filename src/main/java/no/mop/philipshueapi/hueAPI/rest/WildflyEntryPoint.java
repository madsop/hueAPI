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

	@SuppressWarnings("unused")
	@Inject
	private Logger logger;

	@GET
	@Produces("text/plain")
	@Consumes("text/plain")
	@Path("/light/{light}/brightness/{brightness}")
	public Response switchStateOfLight(@PathParam("light") int lightIndex, @PathParam("brightness") int brightness) {
		philipsHueController.setup();

		waitUntilBridgeIsSelected();
		try {
			philipsHueController.switchStateOfGivenLight(sdk.getSelectedBridge(), lightIndex, brightness);
			return Response.ok(getResponseText(lightIndex, brightness)).build();
		}
		catch (HueAPIException e) {
			logger.error(e);
			return Response.ok(e.getMessage()).build();
		}
	}

	@GET
	@Produces("text/plain")
	@Consumes("text/plain")
	@Path("/lights")
	public Response getNumberOfLights() {
		philipsHueController.setup();
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
		return "The new brightness of light " + lightIndex + " is " + newBrightness;
	}
}