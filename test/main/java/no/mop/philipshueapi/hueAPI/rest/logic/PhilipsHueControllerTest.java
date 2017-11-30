package no.mop.philipshueapi.hueAPI.rest.logic;

import com.philips.lighting.hue.sdk.PHNotificationManager;
import no.mop.philipshueapi.hueAPI.rest.HueProperties;
import no.mop.philipshueapi.hueAPI.rest.sdk.SDKFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhilipsHueControllerTest {

    @Mock
    private SDKFacade sdkFacade;

    @Mock
    private PHNotificationManager phNotificationManager;

    @Mock
    private HueProperties hueProperties;

    @Mock
    private BridgeConnector bridgeConnector;

    @Spy
    private Listener listener;

    @InjectMocks
    private PhilipsHueController philipsHueController;

    @Before
    public void setUp() {
        Mockito.doReturn(phNotificationManager).when(sdkFacade).getNotificationManager();
    }

    @Test
    public void t() {
        philipsHueController.run();
    }

}