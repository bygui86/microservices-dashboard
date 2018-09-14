
package be.ordina.msdashboard.security.inbound;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;


public class InboundAuthorizationHeaderCaptorFilterTest {

	private InboundAuthorizationHeaderCaptorFilter filter = new InboundAuthorizationHeaderCaptorFilter();;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private MockFilterChain filterChain;

	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
	}

	@After
	public void tearDown() {
		AuthorizationHeaderHolder.set(null);
	}

	@Test
	public void filterBearerAuthentication() throws Exception {
		addBearerSecurity();
		filter.doFilter(request, response, filterChain);
		assertBearerAuthentication();
	}

	@Test
	public void filterBasicAuthentication() throws Exception {
		addBasicSecurity();
		filter.doFilter(request, response, filterChain);
		assertBasicAuthentication();
	}

	@Test
	public void authFilterWithoutHeaderAndWithFilterShouldNotFilter() throws Exception {
		filter.doFilter(request, response, filterChain);
		assertNoAuthentication();
	}

	@Test
	public void authFilterWithWrongHeaderShouldPass() throws Exception {
		request.addHeader("Authorization", "wrongheader");
		filter.doFilter(request, response, filterChain);
		assertBadAuthentication();
	}

	@Test
	public void authFilterWithIncompleteBasicHeaderShouldPass() throws Exception {
		request.addHeader("Authorization", "Basi eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
		filter.doFilter(request, response, filterChain);
		assertBadAuthentication();
	}

	@Test
	public void authFilterWithIncompleteBearerHeaderShouldPass() throws Exception {
		request.addHeader("Authorization", "Beare eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
		filter.doFilter(request, response, filterChain);
		assertBadAuthentication();
	}

	private void addBearerSecurity() {
		request.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
	}

	private void addBasicSecurity() {
		request.addHeader("Authorization", "Basic eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
	}

	private void assertBasicAuthentication() {
		String retrievedHeader = AuthorizationHeaderHolder.get();
		assertThat(retrievedHeader).startsWith("Basic ");
	}

	private void assertBearerAuthentication() {
		String retrievedHeader = AuthorizationHeaderHolder.get();
		assertThat(retrievedHeader).startsWith("Bearer ");
	}

	private void assertNoAuthentication() {
		String retrievedHeader = AuthorizationHeaderHolder.get();
		assertThat(retrievedHeader).isNull();
	}

	private void assertBadAuthentication() {
		String retrievedHeader = AuthorizationHeaderHolder.get();
		assertThat(retrievedHeader).doesNotStartWith("Basic ").doesNotStartWith("Bearer ");
	}
}
