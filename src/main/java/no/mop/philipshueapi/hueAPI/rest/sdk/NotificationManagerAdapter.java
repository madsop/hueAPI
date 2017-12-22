package no.mop.philipshueapi.hueAPI.rest.sdk;

import no.mop.philipshueapi.hueAPI.rest.logic.Listener;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@SuppressWarnings("unused")
public class NotificationManagerAdapter {

    @Inject
    private SDKFacade sdk;

    @Inject
    private Listener listener;

    @PreDestroy
    public void tearDown() {
        unregisterSDKListener();
    }

    private void unregisterSDKListener() {
        sdk.getNotificationManager().unregisterSDKListener(listener);
    }

    public void registerSDKListener() {
        sdk.getNotificationManager().registerSDKListener(listener);
    }
}
