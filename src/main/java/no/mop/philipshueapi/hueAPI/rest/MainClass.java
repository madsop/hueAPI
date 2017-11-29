package no.mop.philipshueapi.hueAPI.rest;

public class MainClass {

    public static void main(String[] args) throws InterruptedException {
        System.loadLibrary("huesdk");

        new PhilipsHueController().run();
    }
}
