package be.ordina.msdashboard.security.outbound;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author Andreas Evers
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2TokenStrategyTest {

    private OAuth2TokenStrategy oAuth2TokenStrategy = new OAuth2TokenStrategy();

    @Mock
    private HttpClientRequest<ByteBuf> request;
    @Mock
    private OAuth2Authentication auth;
    @Mock
    private OAuth2AuthenticationDetails details;

    @Before
    public void onSetup() {
        when(auth.getDetails()).thenReturn(details);
        when(details.getTokenType()).thenReturn("token");
        when(details.getTokenValue()).thenReturn("value");
    }

    @Test
    public void applies() throws Exception {
        oAuth2TokenStrategy.apply(request, auth);
        verify(request).withHeader("Authorization", "token value");
    }

    @Test
    public void verifyGetType() throws Exception {
       assertThat(oAuth2TokenStrategy.getType()).isEqualTo("forward-oauth2-token");
    }

    @Test
    public void appliesWithNullTokenType() throws Exception {
        when(details.getTokenType()).thenReturn(null);
        oAuth2TokenStrategy.apply(request, auth);
        verify(request).withHeader("Authorization", "Bearer value");
    }

    @Test
    public void doesntApplyWithNullHeader() throws Exception {
        oAuth2TokenStrategy.apply(request, null);
        verify(request, times(0)).withHeader("Authorization", "token value");
    }

    @Test
    public void doesntApplyWithWrongDetails() throws Exception {
        when(auth.getDetails()).thenReturn("wrong details type");
        oAuth2TokenStrategy.apply(request, auth);
        verify(request, times(0)).withHeader("Authorization", "token value");
    }
}
