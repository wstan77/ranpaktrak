package com.lbisoft.core.framework.auth;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.btpplatform.service.BtpPlatformService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.service.AppUserDetailsService;
import com.lbisoft.core.app.settings.AuthenticationSettings;
import com.lbisoft.core.framework.exceptions.InsufficientPermissionException;

public class JwtUserAuthenticationProvider implements Converter<Jwt, JwtAuthenticationToken> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    final AppUserDetailsService userDetailsService;
    final BtpPlatformService btpPlatformService;

    @Autowired
    AuthenticationSettings authSettings;

    public JwtUserAuthenticationProvider(AppUserDetailsService userDetailsService,
            BtpPlatformService btpPlatformService) {
        this.userDetailsService = userDetailsService;
        this.btpPlatformService = btpPlatformService;
    }

    public boolean isOpenID(Jwt source) {
        var issuer = source.getClaimAsString("iss");
        return issuer != null && issuer.contains("accounts.ondemand.com");
    }

    private String getUsername(Jwt source) {
        var username = isOpenID(source) ? source.getClaimAsString("email") : source.getClaimAsString("upn");
        return username;
    }

    @Override
    @Nullable
    public JwtAuthenticationToken convert(@NonNull Jwt source) {
        var username = getUsername(source);
        if (!checkUserIsAuthorized(username, source)) {
            throw new InsufficientPermissionException("User " + username + " is not authorized to use Ranpaktrak");
        }
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            user = createUser(source);
        }
        var newToken = new JwtAuthenticationToken(source, user.getAuthorities());
        newToken.setDetails(user);
        return newToken;
    }

    private UserDetails createUser(Jwt source) {
        AppUser newUser = new AppUser();
        newUser.setUsername(getUsername(source));
        newUser.setEmailAddress(newUser.getUsername());
        newUser.setFirstName(source.getClaimAsString("given_name"));
        newUser.setLastName(source.getClaimAsString("family_name"));
        newUser.setAnonymousFlg(false);
        newUser.setRecordStatusLk(RecordStatusLks.APPROVED);
        if (isOpenID(source)) {
            newUser.setDistributorFlg(true);
            // newUser.setDistributorCustomerId();
        }
        return userDetailsService.createUser(newUser);
    }

    private boolean checkUserIsAuthorized(String username, Jwt source) {
        // for distributors, check authorization to access Ranpaktrak vs the distributor
        // portal
        if (isOpenID(source)) {
            return btpPlatformService.authorizeUser(username);
        }
        // otherwise, for ranpak.com users, check group membership
        else {
            List<String> groups = source.getClaimAsStringList("groups");
            return groups.contains(authSettings.getRanpakUserGroupId());
        }
    }

}
