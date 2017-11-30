package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.hue.sdk.PHHueSDK;

public class MainClass {

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        new PhilipsHueController(PHHueSDK::create).run();
    }
}
