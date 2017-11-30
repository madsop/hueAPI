package no.mop.philipshueapi.hueAPI.rest;

import no.mop.philipshueapi.hueAPI.rest.logic.PhilipsHueController;

import javax.inject.Inject;

public class MainClass {

    @Inject
    private PhilipsHueController philipsHueController;

    public static void main(String[] args) {
        new MainClass().start();
    }

    private void start() {
        philipsHueController.run();
    }
}
