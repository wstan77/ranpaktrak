package com.lbisoft.core.framework.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.lbisoft.core.framework.models.Tuple;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class ProviderBuilder<A, B> {

  private List<Tuple<String, String>> fields = new ArrayList<>();
  private ChildrenMap<B> childrenMap = new ChildrenMap<>();
  private Class<A> classA;
  private Class<B> classB;

  public ProviderBuilder(Class<A> classA, Class<B> classB) {
    this.classA = classA;
    this.classB = classB;
  }

  public static <A, B> AbstractProvider<A, B> simple(Class<A> classA, Class<B> classB) {
    return new ProviderBuilder<A, B>(classA, classB).build();
  }

  public ProviderBuilder<A, B> field(String from, String to) {
    fields.add(new Tuple<>(from, to));
    return this;
  }

  public <C> ProviderBuilder<A, B> defineList(
      Class<C> childClass,
      BiConsumer<B, List<C>> setChildrenList,
      BiPredicate<B, C> childBelongs) {
    childrenMap.defineList(childClass, setChildrenList, childBelongs);
    return this;
  }

  public <C, K> ProviderBuilder<A, B> defineMap(
      Class<C> childClass,
      BiConsumer<B, Map<K, C>> setChildrenMap,
      Function<C, K> getChildKey,
      BiPredicate<B, C> childBelongs) {
    childrenMap.defineMap(childClass, setChildrenMap, getChildKey, childBelongs);
    return this;
  }

  public <C, K> ProviderBuilder<A, B> defineMapofLists(
      Class<C> childClass,
      BiConsumer<B, Map<K, List<C>>> setChildrenMap,
      Function<C, K> getChildKey,
      BiPredicate<B, C> childBelongs) {
    childrenMap.defineMapofLists(childClass, setChildrenMap, getChildKey, childBelongs);
    return this;
  }

  public <C> ProviderBuilder<A, B> defineOneToOne(Class<C> childClass, BiConsumer<B, C> setChild,
      BiPredicate<B, C> childBelongs) {
    childrenMap.defineOneToOne(childClass, setChild, childBelongs);
    return this;
  }

  public AbstractProvider<A, B> build() {
    return new AbstractProvider<A, B>(classA, classB) {

      // Constructor
      {
        childrenMap = ProviderBuilder.this.childrenMap;
      }

      @Override
      protected void defineClassMap(ClassMapBuilder<A, B> classMapBuilder) {
        fields.forEach(tuple -> classMapBuilder.field(tuple.getLeft(), tuple.getRight()));
      }

      @Override
      protected void defineChildren(ChildrenMap<B> childrenMap) {
      }
    };
  }
}
