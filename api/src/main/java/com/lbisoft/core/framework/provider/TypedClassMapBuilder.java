package com.lbisoft.core.framework.provider;

import ma.glasnost.orika.DefaultFieldMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.ClassMapBuilderFactory;
import ma.glasnost.orika.metadata.MappingDirection;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.PropertyResolverStrategy;

public class TypedClassMapBuilder<A, B> extends ClassMapBuilder<A, B> {

  private final MapperFactory currentMapperFactory;

  protected TypedClassMapBuilder(
      Type<A> typeA,
      Type<B> typeB,
      MapperFactory mapperFactory,
      PropertyResolverStrategy propertyResolver,
      DefaultFieldMapper... defaults) {
    super(typeA, typeB, mapperFactory, propertyResolver, defaults);
    currentMapperFactory = mapperFactory;
  }

  private boolean typesAreCompatible(String propNameA, String propNameB) {
    Property propA = resolvePropertyForA(propNameA);
    Property propB = resolvePropertyForB(propNameB);

    Type<?> typeA = propA.getType();
    Type<?> typeB = propB.getType();

    if (typeA.equals(typeB)
        || currentMapperFactory.getConverterFactory().canConvert(typeA, typeB)) {
      return true;
    } else if (typeB.isPrimitiveFor(typeA) || typeA.isPrimitiveFor(typeB)) {
      return true;
    }

    return false;
  }

  /**
   * Configures this class-map builder to employ the default property mapping behavior to any
   * properties that have not already been mapped or excluded; if any DefaultFieldMapper instances
   * are passed, they will be used (instead of those configured on the builder) to attempt a
   * property name match if a direct match is not found.
   *
   * @param direction the mapping direction to be applied
   * @param withDefaults zero or more DefaultFieldMapper instances to apply during the default
   *     mapping; if none are supplied, the configured DefaultFieldMappers for the builder (if any)
   *     should be used.
   * @return this ClassMapBuilder instance
   */
  @Override
  public ClassMapBuilder<A, B> byDefault(
      MappingDirection direction, DefaultFieldMapper... withDefaults) {
    /* Slightly tweaked from ClassMapBuilder, prevents default mapping between incompatible types */

    DefaultFieldMapper[] defaults;
    if (withDefaults.length == 0) {
      defaults = getDefaultFieldMappers();
    } else {
      defaults = withDefaults;
    }

    for (final String propertyName : getPropertiesForTypeA()) {
      if (!getMappedPropertiesForTypeA().contains(propertyName)
          && getPropertiesForTypeB().contains(propertyName)
          && !getMappedPropertiesForTypeB().contains(propertyName)
          /*
           * Don't include the default mapping of Class to Class; this
           * property is resolved for all types, but can't be mapped
           * in either direction.
           */
          && !"class".equals(propertyName)
          && typesAreCompatible(propertyName, propertyName)) {
        fieldMap(propertyName, true).direction(direction).add();
      } else {
        Property prop = resolvePropertyForA(propertyName);
        for (DefaultFieldMapper defaulter : defaults) {
          String suggestion = defaulter.suggestMappedField(propertyName, prop.getType());
          if (suggestion != null
              && getPropertiesForTypeB().contains(suggestion)
              && !getMappedPropertiesForTypeB().contains(suggestion)
              && typesAreCompatible(propertyName, suggestion)) {
            fieldMap(propertyName, suggestion, true).direction(direction).add();
          }
        }
      }
    }

    return this;
  }

  public static class Factory extends ClassMapBuilderFactory {
    @Override
    protected <A, B> ClassMapBuilder<A, B> newClassMapBuilder(
        Type<A> typeA,
        Type<B> typeB,
        MapperFactory mapperFactory,
        PropertyResolverStrategy propertyResolver,
        DefaultFieldMapper[] defaults) {

      return new TypedClassMapBuilder<A, B>(
          typeA, typeB, mapperFactory, propertyResolver, defaults);
    }
  }
}
