package com.lbisoft.core.app.components.sugarcrm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequest {
    String username;
    String password;
}
