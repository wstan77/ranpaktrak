package com.lbisoft.core.framework.auth;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.core.userdetails.UserDetails;

import com.lbisoft.core.framework.user.JwtUser;
import com.lbisoft.core.framework.utils.BooleanHelper;
import com.lbisoft.core.framework.utils.DateHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtil {
  private static final SignatureAlgorithm SIGNATURE_ALGO = SignatureAlgorithm.HS512;
  private static final long MAX_TIMEOUT = 24 * 60 * 60L;

  public static enum ClaimKey {
    USERNAME("upn"), IMPERSONATED_BY("impersonated_by"), DEVICE_ID("device_id"), CREATED("created"), ISSUER("iss");

    String field;

    private ClaimKey(String field) {
      this.field = field;
    }

    public String getField() {
      return field;
    }
  }

  private String secret;

  public JwtTokenUtil(String secret) {
    this.secret = secret;
  }

  public String getUsername(String token) {
    return getUsername(getClaimsFromToken(token));
  }

  private String getUsername(Claims claims) {
    if (claims == null)
      return null;
    return (String) claims.get(ClaimKey.USERNAME.getField());
  }

  public String getDeviceId(String token) {
    return getDeviceId(getClaimsFromToken(token));
  }

  private String getDeviceId(Claims claims) {
    if (claims == null)
      return null;
    return (String) claims.get(ClaimKey.DEVICE_ID.getField());
  }

  public Integer getImpersonatedBy(String token) {
    return getImpersonatedBy(getClaimsFromToken(token));
  }

  private Integer getImpersonatedBy(Claims claims) {
    if (claims == null)
      return null;
    return (Integer) claims.get(ClaimKey.IMPERSONATED_BY.getField());
  }

  public Instant getCreatedDate(String token) {
    return getCreatedDate(getClaimsFromToken(token));
  }

  public Instant getCreatedDate(Claims claims) {
    if (claims == null)
      return null;
    final Long time = (Long) claims.get(ClaimKey.CREATED.getField());
    if (time != null) {
      return DateHelper.fromMillisecondsSinceEpoch(time);
    } else {
      return null;
    }
  }

  public boolean isExpired(String token) {
    return isExpired(getClaimsFromToken(token));
  }

  private boolean isExpired(Claims claims) {
    if (claims == null)
      return true;

    Instant expirationTime = DateHelper.asInstant(claims.getExpiration());
    return Instant.now().isAfter(expirationTime.plusSeconds(60));
  }

  public boolean needsRenewal(String token) {
    return needsRenewal(getClaimsFromToken(token));
  }

  private boolean needsRenewal(Claims claims) {
    if (claims == null)
      return false;

    return !isExpired(claims);
  }

  private Claims getClaimsFromToken(String token) {
    if (token != null) {
      token = token.replace("Bearer ", "");
    }
    Claims claims;
    try {
      claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      claims = null;
    }
    return claims;
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    JwtUser user = (JwtUser) userDetails;
    final Claims claims = getClaimsFromToken(token);
    final String username = getUsername(claims);

    return (username.equals(user.getUsername()) && !isExpired(claims));
  }

  private String generateToken(Map<String, Object> claims, long secondsToExpire) {
    return Jwts.builder().setClaims(claims).setExpiration(Date.from(generateExpirationDate(secondsToExpire)))
        .signWith(SIGNATURE_ALGO, secret).compact();
  }

  public String generateToken(UserDetails userDetails, String issuer, long secondsToExpire) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(ClaimKey.ISSUER.getField(), issuer);
    claims.put(ClaimKey.USERNAME.getField(), userDetails.getUsername());
    claims.put(ClaimKey.CREATED.getField(), DateHelper.asMillisecondsSinceEpoch(Instant.now()));
    return generateToken(claims, hasExtendedTimeout(userDetails) ? MAX_TIMEOUT : secondsToExpire);
  }

  public String generateImpersonationToken(UserDetails userDetails, Integer currentUserId, String issuer, long secondsToExpire) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(ClaimKey.ISSUER.getField(), issuer);
    claims.put(ClaimKey.USERNAME.getField(), userDetails.getUsername());
    claims.put(ClaimKey.IMPERSONATED_BY.getField(), currentUserId);
    claims.put(ClaimKey.CREATED.getField(), DateHelper.asMillisecondsSinceEpoch(Instant.now()));
    return generateToken(claims, hasExtendedTimeout(userDetails) ? MAX_TIMEOUT : secondsToExpire);
  }

  private boolean hasExtendedTimeout(UserDetails userDetails) {
    return userDetails instanceof JwtUser
        && BooleanHelper.isTrue(((JwtUser) userDetails).getUser().getExtendedTimeoutFlg());
  }

  private Instant generateExpirationDate(long secondsToExpire) {
    secondsToExpire = Math.min(secondsToExpire, MAX_TIMEOUT);
    return Instant.now().plusSeconds(secondsToExpire);
  }

  public SecretKey getSecretKey() {
    // Parse base64 encoded secret
    byte[] secretBytes = Base64.decodeBase64(secret);

    SecretKey secretKey = new SecretKeySpec(secretBytes, SIGNATURE_ALGO.getJcaName());
    return secretKey;
  }
}
