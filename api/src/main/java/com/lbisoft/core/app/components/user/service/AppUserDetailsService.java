package com.lbisoft.core.app.components.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.user.JwtUser;

@CoreService
@Transactional(readOnly = true)
public class AppUserDetailsService
        implements org.springframework.security.core.userdetails.UserDetailsService {
    private static final String BAD_CREDENTIALS_MESSAGE = "Email and Password are correct, but no account is associated with your email: %s";
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String name = username;
        return userService.getUserByUsername(name).map(JwtUser::new)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format(BAD_CREDENTIALS_MESSAGE, name)));
    }

    public UserDetails createUser(AppUser userToCreate) {
        AppUser user = userService.createUser(userToCreate, null);
        userRoleService.assignRoleToUser(
                user.isDistributorFlg() ? Constants.Roles.DISTRIBUTOR : Constants.Roles.RANPAKUSER, user);
        return new JwtUser(user);
    }
}
