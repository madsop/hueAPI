package no.mop.philipshueapi.hueAPI.rest;

public class MainClass {

    public static void main(String[] args) {
        start();
    }

    static void start() {
        new PhilipsHueController().run();
    }
}
