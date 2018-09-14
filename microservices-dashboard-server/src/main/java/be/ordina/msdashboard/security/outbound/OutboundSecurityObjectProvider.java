package be.ordina.msdashboard.security.outbound;

import be.ordina.msdashboard.security.inbound.AuthorizationHeaderHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provider for outbound security objects, either an authentication header, or a full-blown Spring Security Authentication object
 *
 *
 * @author Andreas Evers
 */
public class OutboundSecurityObjectProvider {

    /**
     * Retrieves a security object, needed for outbound traffic of the aggregators
     *
     * @return Either an Authentication header stored in the AuthorizationHeaderHolder,
     * or a Spring Security Authentication object stored in the SecurityContextHolder
     */
    public Object getOutboundSecurityObject() {
        Object outboundSecurityObject;
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String authorizationHeader = AuthorizationHeaderHolder.get();
        if (authorizationHeader != null) {
            outboundSecurityObject = authorizationHeader;
        } else {
            outboundSecurityObject = auth;
        }
        return outboundSecurityObject;
    }
}
