package no.mop.philipshueapi.hueAPI.rest;

import com.philips.lighting.model.PHHueParsingError;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class Logger {
    public void error(HueAPIException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
    }

    public void logParsingErrors(Collection<PHHueParsingError> errors) {
        errors.forEach(System.err::println);
    }

    public void warn(String message) {
        System.out.println(message);
    }

    public void fine(String message) {

    }
}
