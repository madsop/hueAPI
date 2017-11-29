package no.mop.philipshueapi.hueAPI.rest;

public class MainClass {

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        new PhilipsHueController().run();
    }
}
