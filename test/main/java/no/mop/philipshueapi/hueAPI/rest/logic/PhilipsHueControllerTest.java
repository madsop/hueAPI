package no.mop.philipshueapi.hueAPI.rest.logic;

import com.philips.lighting.hue.sdk.PHNotificationManager;
import com.philips.lighting.hue.sdk.utilities.impl.Color;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

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
    public void colorConversionTest() {
        int magentaHue = Color.MAGENTA;
        java.awt.Color magentaJava = java.awt.Color.MAGENTA;
        int rgb = Color.rgb(magentaJava.getRed(), magentaJava.getGreen(), magentaJava.getBlue());
        assertEquals(magentaHue, rgb);
    }

    @Test
    public void wontInvokeNonReachableLight() {
        philipsHueController = spy(philipsHueController);
        PHLight nonReachableLight = mock(PHLight.class);
        PHLightState nonReachableLightsState = mock(PHLightState.class);
        doReturn(nonReachableLightsState).when(nonReachableLight).getLastKnownLightState();
        doReturn(nonReachableLight).when(philipsHueController).getGivenLight(any(), anyInt());

        philipsHueController.switchStateOfGivenLight(null, 0, 0);

        verify(nonReachableLightsState, never()).setBrightness(any());
    }

}