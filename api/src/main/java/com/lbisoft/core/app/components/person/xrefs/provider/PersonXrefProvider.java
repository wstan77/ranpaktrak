package com.lbisoft.core.app.components.person.xrefs.provider;

import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.db.model.generated.PersonXrefDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class PersonXrefProvider extends AbstractProvider<PersonXrefDb, PersonXref> {

  @Override
  protected void defineClassMap(ClassMapBuilder<PersonXrefDb, PersonXref> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<PersonXref> childrenMap) {
  }

  public PersonXrefDb completeReverseMap(PersonXref input, PersonXrefDb output) {
    return output;
  }
}