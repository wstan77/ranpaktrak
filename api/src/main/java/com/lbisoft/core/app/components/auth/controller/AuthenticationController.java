package com.lbisoft.core.app.components.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.auth.model.AnonymousAuthRequest;
import com.lbisoft.core.app.components.auth.model.AuthRequest;
import com.lbisoft.core.app.components.auth.model.AuthToken;
import com.lbisoft.core.app.components.auth.model.ImpersonationRequest;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.service.UserService;
import com.lbisoft.core.app.settings.AuthenticationSettings;
import com.lbisoft.core.framework.auth.JwtTokenUtil;
import com.lbisoft.core.framework.exceptions.MethodNotAllowedException;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  AuthenticationSettings authSettings;
  @Autowired
  UserService userService;

  private long getJWTSecondsToExpire() {
    return 30 * 60;
  }

  @PostMapping(value = "/anonymous")
  public ResponseEntity<AuthToken> createAnonymousAuthenticationToken(@RequestBody AnonymousAuthRequest authRequest, HttpServletRequest request) {
    String uuid = authRequest.getUuid();
    if (uuid == null) {
      AppUser newUser = userService.createAnonymousUser();
      uuid = newUser.getUsername();
    } else {
      // Confirm that the uuid matches an anonymous user
      boolean isAnonymous = userService.getUserByUsername(uuid).map(user -> user.isAnonymousFlg()).orElse(false);
      if (!isAnonymous) {
        throw new MethodNotAllowedException("Invalid uuid");
      }
    }

    final UserDetails userDetails = userDetailsService.loadUserByUsername(uuid);
    final String token = jwtTokenUtil.generateToken(userDetails, authSettings.getAnonymousIssuer(), getJWTSecondsToExpire());

    // Return the token
    return ResponseEntity.ok(new AuthToken(token));
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<AuthToken> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest,
      HttpServletResponse response) {

    // Perform the security
    final Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authenticationRequest.getEmail(),
            authenticationRequest.getPassword()));
    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(authentication);

      final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
      final String token = jwtTokenUtil.generateToken(userDetails, authSettings.getAnonymousIssuer(), getJWTSecondsToExpire());

      // Return the token
      return ResponseEntity.ok(new AuthToken(token));
    }
    return null;
  }

  @Operation(summary = "", hidden = true)
  @RequestMapping(value = "/impersonate", method = RequestMethod.POST)
  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
  public ResponseEntity<AuthToken> impersonate(@RequestBody final ImpersonationRequest impersonationRequest,
      final HttpServletRequest request) {
    final UserDetails userDetails = userDetailsService.loadUserByUsername(impersonationRequest.getUsername());
    final Integer trueUserId = RequestHelper.getTrueUserId(request);
    if (trueUserId != null) {
      final String token = jwtTokenUtil.generateImpersonationToken(userDetails, trueUserId, authSettings.getAnonymousIssuer(), getJWTSecondsToExpire());

      // Return the token
      return ResponseEntity.ok(new AuthToken(token));
    }
    return null;
  }

  @RequestMapping(value = "/renew", method = RequestMethod.POST)
  public AuthToken renewAuthenticationToken(final HttpServletRequest request) {
    // TODO: Require a "Refresh Token" a UUID that is stored in the database and
    // confirm that the given refresh token matches the one in the database for the current user
    // then generate a new refresh token and return it along with the access token
    // If the refresh token is not valid, then return a 401 and clear the user's refresh token
    // to force them to login again
    final String currToken = request.getHeader(authSettings.getJwtHeader());
    if (currToken != null && jwtTokenUtil.needsRenewal(currToken)) {
      final String username = jwtTokenUtil.getUsername(currToken);
      final Integer impersonator = jwtTokenUtil.getImpersonatedBy(currToken);
      final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      String token = null;
      if (impersonator != null) {
        token = jwtTokenUtil.generateImpersonationToken(userDetails, impersonator, authSettings.getAnonymousIssuer(), getJWTSecondsToExpire());
      } else {
        token = jwtTokenUtil.generateToken(userDetails, authSettings.getAnonymousIssuer(), getJWTSecondsToExpire());
      }

      return new AuthToken(token);
    }
    return new AuthToken();
  }
}
