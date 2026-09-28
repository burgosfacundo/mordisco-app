package utn.back.mordiscoapi.security.jwt.utils;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import utn.back.mordiscoapi.service.impl.UsuarioServiceImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {
    private static final String USERNAME = "user@example.com";
    private static final String ACCESS_TOKEN = "valid-access-token";

    @Mock
    private UsuarioServiceImpl userService;
    @Mock
    private JwtUtil jwtUtil;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validAccessTokenDoesNotAuthenticateDisabledUserDetails() throws Exception {
        UserDetails disabledUser = User.withUsername(USERNAME)
                .password("password")
                .authorities("ROLE_CLIENTE")
                .disabled(true)
                .build();
        when(jwtUtil.extractUserName(ACCESS_TOKEN)).thenReturn(USERNAME);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(disabledUser);
        when(jwtUtil.isAccessTokenValid(ACCESS_TOKEN, disabledUser)).thenReturn(true);
        FilterChain filterChain = mock(FilterChain.class);
        MockHttpServletRequest request = authenticatedRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtRequestFilter(userService, jwtUtil).doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).isAccessTokenValid(ACCESS_TOKEN, disabledUser);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void validAccessTokenAuthenticatesEnabledUserDetails() throws Exception {
        UserDetails enabledUser = User.withUsername(USERNAME)
                .password("password")
                .authorities("ROLE_CLIENTE")
                .build();
        when(jwtUtil.extractUserName(ACCESS_TOKEN)).thenReturn(USERNAME);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(enabledUser);
        when(jwtUtil.isAccessTokenValid(ACCESS_TOKEN, enabledUser)).thenReturn(true);
        FilterChain filterChain = mock(FilterChain.class);
        MockHttpServletRequest request = authenticatedRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtRequestFilter(userService, jwtUtil).doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertSame(enabledUser, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    private MockHttpServletRequest authenticatedRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + ACCESS_TOKEN);
        return request;
    }
}
