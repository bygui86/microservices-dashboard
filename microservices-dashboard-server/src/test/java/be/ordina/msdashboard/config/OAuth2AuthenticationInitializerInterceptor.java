package be.ordina.msdashboard.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request interceptor for initialising an OAuth2 context for testing
 *
 * @author Kevin Van Houtte
 */
public class OAuth2AuthenticationInitializerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Authentication user = new UsernamePasswordAuthenticationToken("user", "password");
		AuthorizationRequest authorizationRequest = new AuthorizationRequest();
		authorizationRequest.setClientId("client");
		OAuth2Request oAuth2Request = authorizationRequest.createOAuth2Request();
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, user);
		request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, "Bearer");
		request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
		oAuth2Authentication.setDetails(new OAuth2AuthenticationDetails(request));
		SecurityContext securityContextHolder = SecurityContextHolder.getContext();
		securityContextHolder.setAuthentication(oAuth2Authentication);
		return true;
	}
}
