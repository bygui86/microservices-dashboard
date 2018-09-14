package be.ordina.msdashboard.security.outbound;

import be.ordina.msdashboard.security.inbound.AuthorizationHeaderHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Andreas Evers
 */
@PrepareForTest(SecurityContextHolder.class)
@RunWith(PowerMockRunner.class)
public class OutboundSecurityObjectProviderTest {

    private OutboundSecurityObjectProvider outboundSecurityObjectProvider = new OutboundSecurityObjectProvider();

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @Before
    public void onSetup() {
        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void getsHeaderWhenBothPresent() throws Exception {
        SecurityContextHolder.getContext().getAuthentication();
        AuthorizationHeaderHolder.set("test");
        assertThat(outboundSecurityObjectProvider.getOutboundSecurityObject()).isEqualTo("test");
        AuthorizationHeaderHolder.set(null);
    }

    @Test
    public void getsHeaderFromDefaultAuthentication() throws Exception {
        SecurityContextHolder.getContext().getAuthentication();
        assertThat(outboundSecurityObjectProvider.getOutboundSecurityObject()).isEqualTo(authentication);
    }

}
