package org.example.javaprojektsystemrezerwacjihotelowej.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.example.javaprojektsystemrezerwacjihotelowej.service.JwtService;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterExtractTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Spy
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractAccessToken_WithNonBearerAuthHeader_ShouldReturnNull() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNzd29yZA==");
        when(request.getCookies()).thenReturn(null);

        // Act
        String token = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "extractAccessToken", request);

        // Assert
        assertNull(token);
        
        // Verify the filter chain is called
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void extractAccessToken_WithEmptyAuthHeader_ShouldReturnNull() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");
        when(request.getCookies()).thenReturn(null);

        // Act
        String token = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "extractAccessToken", request);

        // Assert
        assertNull(token);
        
        // Verify the filter chain is called
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void extractCookie_WithNullCookies_ShouldReturnNull() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String token = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "extractCookie", (Cookie[])null, "accessToken");

        // Assert
        assertNull(token);
        
        // Verify behavior with null cookies
        try {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        } catch (Exception e) {
            fail("Should not throw exception with null cookies");
        }
    }

    @Test
    void extractCookie_WithEmptyCookies_ShouldReturnNull() {
        // Arrange
        when(request.getCookies()).thenReturn(new Cookie[0]);

        // Act
        String token = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "extractCookie", new Cookie[0], "accessToken");

        // Assert
        assertNull(token);
        
        // Verify behavior with empty cookies
        try {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        } catch (Exception e) {
            fail("Should not throw exception with empty cookies");
        }
    }
}