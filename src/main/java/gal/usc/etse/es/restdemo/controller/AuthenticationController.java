package gal.usc.etse.es.restdemo.controller;

import gal.usc.etse.es.restdemo.exception.DuplicateUserException;
import gal.usc.etse.es.restdemo.exception.InvalidRefreshTokenException;
import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.service.AuthenticationService;
import gal.usc.etse.es.restdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.time.Duration;

@NullMarked
@RestController
@RequestMapping("auth")
public class AuthenticationController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "__Secure-RefreshToken";
    private final AuthenticationService authentication;
    private final UserService users;

    @Autowired
    public AuthenticationController(AuthenticationService authentication, UserService users) {
        this.authentication = authentication;
        this.users = users;
    }

    @Operation(
            summary = "Authenticate into the application",
            description = "Authenticates the user into the application and gets a JWT token in the Authorization header"
    )
    @SecurityRequirement(name = "")
    @PostMapping(
            path = "login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> login(@RequestBody User user) {
        User loggedUser = authentication.login(user);
        String refreshToken = authentication.regenerateRefreshToken(user);
        String refreshPath = MvcUriComponentsBuilder.fromMethodName(AuthenticationController.class, "refresh", "").build().toUri().getPath();

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .secure(true)
                .httpOnly(true)
                .sameSite(Cookie.SameSite.STRICT.toString())
                .path(refreshPath)
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.noContent()
                .headers(headers -> headers.setBearerAuth(loggedUser.password()))
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping(
            path = "register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<User> register(@RequestBody User user) throws DuplicateUserException {
        User createdUser = users.create(user);

        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(UserController.class, "get", user.userID()).build().toUri())
                .body(createdUser);
    }

    @PostMapping("refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) throws InvalidRefreshTokenException {
        User user = authentication.login(refreshToken);

        return login(user);
    }

    @PostMapping("logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        User user = authentication.parseJWT(token.replaceFirst("^Bearer ", ""));
        authentication.invalidateTokens(user.userID());
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, null).build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
