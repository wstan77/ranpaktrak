package com.lbisoft.core.framework.provider;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public final class GblMapperFactory {
  private GblMapperFactory() {
  }

  public static MapperFactory getMapperFactory() {
    MapperFactory factory = new DefaultMapperFactory.Builder()
        .classMapBuilderFactory(new TypedClassMapBuilder.Factory()).build();
    return factory;
  }
}
