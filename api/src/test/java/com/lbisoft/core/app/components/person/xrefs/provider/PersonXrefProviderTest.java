package com.lbisoft.core.app.components.person.xrefs.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.db.model.generated.PersonXrefDb;

public class PersonXrefProviderTest {
  private final PersonXrefProvider provider = new PersonXrefProvider();
  private PersonXrefDb db;
  private PersonXref model;

  @BeforeEach
  public void setup() {
    db = new PersonXrefDb();
    db.setPersonId(123);
    db.setSourceId("id");
    db.setSourceSystemLk("source");

    model = new PersonXref();
    model.setPersonId(db.getPersonId());
    model.setSourceId(db.getSourceId());
    model.setSourceSystemLk(db.getSourceSystemLk());
  }

  @Test
  public void shouldConvertDbToModel() {
    PersonXref test = provider.map(db);
    assertThat(test).usingRecursiveComparison().isEqualTo(model);
  }

  @Test
  public void shouldConvertModeltoDb() {
    PersonXrefDb test = provider.reverseMap(model);
    assertThat(test).usingRecursiveComparison().isEqualTo(db);
  }
}
