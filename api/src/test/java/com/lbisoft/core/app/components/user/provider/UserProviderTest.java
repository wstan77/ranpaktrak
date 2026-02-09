package com.lbisoft.core.app.components.user.provider;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.model.UserGroup;
import com.lbisoft.core.app.components.user.model.UserGroupUser;
import com.lbisoft.core.app.db.model.generated.UserDb;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.app.db.model.generated.UserSubordinateDb;
import com.lbisoft.core.framework.permissions.model.UserPermissions;

public class UserProviderTest {
    private final UserProvider provider = new UserProvider();
    private UserDb db;
    private AppUser model;
    private List<UserRoleDb> userRoles;
    private List<UserSubordinateDb> subordinates;
    private List<UserGroupUser> userGroupUsers;
    private UserPermissions userPermissions;

    @BeforeEach
    public void setup() {
        db = new UserDb().withFirstName("First").withLastName("Last").withUserId(123)
                .withRecordStatusLk("RECORD_STATUS")
                .withUsername("USERNAME").withEmailAddress("user@email.com").withLastLoginDate(LocalDateTime.now())
                .withAnonymousFlg(true).withDistributorCustomerId(12345).withDistributorFlg(true);

        // roles
        UserRoleDb userRole = new UserRoleDb();
        userRole.setRoleId(1);
        userRole.setUserId(db.getUserId());
        UserRoleDb badUserRole = new UserRoleDb();
        badUserRole.setRoleId(2);
        badUserRole.setUserId(db.getUserId() + 1);
        userRoles = List.of(userRole, badUserRole);

        // subordinates
        UserSubordinateDb goodSub = new UserSubordinateDb();
        goodSub.setUserId(db.getUserId());
        goodSub.setSubordinateUserId(100);
        UserSubordinateDb badSub = new UserSubordinateDb();
        badSub.setUserId(db.getUserId() + 1);
        badSub.setSubordinateUserId(200);
        subordinates = List.of(goodSub, badSub);

        // groups
        UserGroup group = new UserGroup();
        group.setUserGroupId(1);
        group.setDescription("Good Group");
        UserGroupUser groupUser = new UserGroupUser();
        groupUser.setUserGroup(group);
        groupUser.setUserId(db.getUserId());
        UserGroup badGroup = new UserGroup();
        badGroup.setUserGroupId(2);
        badGroup.setDescription("Bad Group");
        UserGroupUser badGroupUser = new UserGroupUser();
        badGroupUser.setUserGroup(badGroup);
        badGroupUser.setUserId(db.getUserId() + 1);
        userGroupUsers = List.of(groupUser, badGroupUser);

        // permissions
        userPermissions = new UserPermissions();
        userPermissions.setUserId(db.getUserId());

        model = new AppUser();
        model.setFirstName(db.getFirstName());
        model.setLastName(db.getLastName());
        model.setUserId(db.getUserId());
        model.setUsername("USERNAME");
        model.setRecordStatusLk(db.getRecordStatusLk());
        model.setEmailAddress(db.getEmailAddress());
        model.setLastLoginDate(db.getLastLoginDate());
        model.setUserRoles(List.of(userRole));
        model.setSubordinateIds(List.of(goodSub.getSubordinateUserId()));
        model.setUserGroups(List.of(group));
        model.setPermissions(userPermissions);
        model.setAnonymousFlg(true);
        model.setDistributorCustomerId(12345);
        model.setDistributorFlg(true);

    }

    @Test
    public void shouldConvertDbToModel() {
        AppUser test = provider.map(db, userRoles, subordinates, userGroupUsers, List.of(userPermissions));
        assertThat(test).usingRecursiveComparison().isEqualTo(model);
    }

    @Test
    public void shouldConvertModeltoDb() {
        UserDb test = provider.reverseMap(model);
        assertThat(test).usingRecursiveComparison().isEqualTo(db);
    }
}
