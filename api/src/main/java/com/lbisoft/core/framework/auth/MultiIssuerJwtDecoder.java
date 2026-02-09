package com.lbisoft.core.framework.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

public class MultiIssuerJwtDecoder implements JwtDecoder {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultiIssuerJwtDecoder.class);

  private final Map<String, JwtDecoder> decodersByIssuer;

  public MultiIssuerJwtDecoder() {
    decodersByIssuer = new HashMap<>();
  }

  public MultiIssuerJwtDecoder withIssuer(String issuer, JwtDecoder decoder) {
    decodersByIssuer.put(issuer, decoder);
    return this;
  }

  @Override
  public Jwt decode(String token) throws JwtException {
    // Attempt to decode the token with each decoder
    Jwt jwt = null;
    for (var decoder : decodersByIssuer.values()) {
      try {
        jwt = decoder.decode(token);
        if (jwt != null) {
          break;
        }
      } catch (JwtException e) {
        // If the token cannot be decoded by this decoder, try the next one
        LOGGER.debug("Token could not be decoded by decoder for issuer {}: {}", decoder, e);
      }
    }
    if (jwt == null) {
      throw new JwtException("Token could not be decoded by any of the provided decoders");
    }
    return jwt;
  }
}
