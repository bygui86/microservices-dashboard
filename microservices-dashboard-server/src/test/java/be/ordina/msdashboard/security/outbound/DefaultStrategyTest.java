package be.ordina.msdashboard.security.outbound;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Kevin Van Houtte
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultStrategyTest {

    private DefaultStrategy defaultStrategy = new DefaultStrategy();

    @Mock
    private HttpClientRequest<ByteBuf> request;

    @Test
    public void applies() throws Exception {
        defaultStrategy.apply(request, "abc");
        assertThat(request.withHeader("Authorization","abc")).isNull();
    }

    @Test
    public void verifyGetType() throws Exception {
        assertThat(defaultStrategy.getType()).isEqualTo("none");
    }

}
