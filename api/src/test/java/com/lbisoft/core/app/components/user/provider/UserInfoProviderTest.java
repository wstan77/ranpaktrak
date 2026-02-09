package com.lbisoft.core.app.components.user.provider;

import static org.assertj.core.api.Assertions.*;

import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.app.db.model.generated.UserDb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserInfoProviderTest {
  private final UserInfoProvider provider = new UserInfoProvider();
  private UserDb db;
  private UserInfo model;

  @BeforeEach
  public void setup() {
    db = new UserDb().withFirstName("First").withLastName("Last").withUserId(123);

    model = new UserInfo();
    model.setFirstName(db.getFirstName());
    model.setLastName(db.getLastName());
    model.setUserId(db.getUserId());
  }

  @Test
  public void shouldConvertDbToModel() {
    UserInfo test = provider.map(db);
    assertThat(test).usingRecursiveComparison().isEqualTo(model);
  }

  @Test
  public void shouldConvertModeltoDb() {
    UserDb test = provider.reverseMap(model);
    assertThat(test).usingRecursiveComparison().isEqualTo(db);
  }
}
