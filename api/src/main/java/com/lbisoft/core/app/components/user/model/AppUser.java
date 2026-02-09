package com.lbisoft.core.app.components.user.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.app.db.model.generated.UserSubordinateDb;
import com.lbisoft.core.framework.permissions.model.UserPermissions;
import com.lbisoft.core.framework.user.IRole;
import com.lbisoft.core.framework.user.IUser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppUser extends UserInfo implements IUser, Serializable {
    private static final long serialVersionUID = -34438708238930098L;

    private String username;
    private String emailAddress;
    private UserPermissions permissions = new UserPermissions();
    private List<Integer> subordinateIds = Collections.emptyList();
    private Boolean registeredFlg = false;
    private Boolean extendedTimeoutFlg = false;
    private Integer impersonatedBy;
    private List<UserRoleDb> userRoles = Collections.emptyList();
    private LocalDateTime lastLoginDate;
    private List<UserGroup> userGroups = Collections.emptyList();
    private boolean anonymousFlg = false;
    private boolean distributorFlg = false;
    private Integer distributorCustomerId;

    public AppUser() {
    }

    /**
     * Set subordinate ids based on a list of User-Subordinate beans
     *
     * @param subordinates
     */
    public void setSubordinates(List<UserSubordinateDb> subordinates) {
        this.subordinateIds = subordinates.stream().map(UserSubordinateDb::getSubordinateUserId).toList();
    }

    public void setUserGroupUsers(List<UserGroupUser> userGroupUsers) {
        if (userGroupUsers == null) {
            this.userGroups = Collections.emptyList();
        } else {
            this.userGroups = userGroupUsers.stream().map(UserGroupUser::getUserGroup).toList();
        }
    }

    public boolean isActive() {
        return RecordStatusLks.APPROVED.equals(getRecordStatusLk());
    }

    @Override
    public List<? extends IRole> getRoles() {
        return Collections.emptyList();
    }

}
