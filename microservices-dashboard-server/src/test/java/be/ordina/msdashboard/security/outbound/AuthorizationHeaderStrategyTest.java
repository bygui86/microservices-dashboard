package be.ordina.msdashboard.security.outbound;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author Andreas Evers
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationHeaderStrategyTest {

    private AuthorizationHeaderStrategy authorizationHeaderStrategy = new AuthorizationHeaderStrategy();

    @Mock
    private HttpClientRequest<ByteBuf> request;

    @Test
    public void applies() throws Exception {
        authorizationHeaderStrategy.apply(request, "abc");
        verify(request).withHeader("Authorization", "abc");
    }

    @Test
    public void doesntApplyWithNullHeader() throws Exception {
        authorizationHeaderStrategy.apply(request, null);
        verify(request, times(0)).withHeader("Authorization", "abc");
    }

    @Test
    public void verifyGetType() throws Exception {
        assertThat(authorizationHeaderStrategy.getType()).isEqualTo("forward-inbound-auth-header");
    }


}
