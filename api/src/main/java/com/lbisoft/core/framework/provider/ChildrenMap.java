package com.lbisoft.core.framework.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lbisoft.core.framework.models.Tuple;

/**
 * Used to simplify mapping children to fields, lists, and maps in the parent
 * model.
 *
 * <p>
 * Setup:
 *
 * <p>
 * Create a ChildrenMap for the parent model and use the {@code define} methods
 * to configure
 * how the children get mapped to the parent.
 *
 * <p>
 * Use:
 *
 * <p>
 * Call {@link #mapChildren(Object, Object...)} or
 * {@link #mapChildrenAll(List, List...)}
 * to map children models to their corresponding parent.
 *
 * <p>
 * Avoid instantiating ChildrenMap for every call to map children. Re-use the
 * ChildrenMap when mapping
 * to the same parent model.
 *
 * @param <P> The type of the parent model receiving the children.
 */
public class ChildrenMap<P> {
  private final List<ChildMapper<?>> childMappers;

  private final Set<Class<?>> classesMapped = new HashSet<>();
  private boolean hasMultipleOfSameClass = false;

  public ChildrenMap() {
    childMappers = new ArrayList<>();
  }

  /**
   * An easier way to create a lambda for testing if a child belongs, avoiding
   * null pointer exceptions.
   *
   * <p>
   * Example:
   *
   * <pre>
   * {@code
   * ChildrenMap.defineOneToOne(
   *     Child.class,
   *     Parent::setChild,
   *     ChildrenMap.match(
   *         parent -> parent.map(Parent::getFoo).map(Foo::getChildId),
   *         child -> child.map(Child::getChildId)))
   * }
   * </pre>
   *
   * @param <P>          Parent Type
   * @param <C>          Child Type
   * @param <T>          Type that both getters return
   *
   * @param parentGetter Function that receives an Optional of the parent model
   *                     and returns an Optional to match on.
   * @param childGetter  Function that receives an Optional of the potential child
   *                     model and returns and Optional to match on.
   * @return A function that runs the getters for parent and child and returns
   *         true if the results pass {@link Optional#equals(Object)}.
   */
  public static <P, C, T> BiPredicate<P, C> match(
      Function<Optional<P>, Optional<T>> parentGetter,
      Function<Optional<C>, Optional<T>> childGetter) {
    return (P parent, C child) -> parentGetter.apply(Optional.of(parent)).equals(childGetter.apply(Optional.of(child)));
  }

  /**
   * Get a lamdba that tests if a child belongs to a parent that already contains
   * a child model populated
   * with the identifying field.
   *
   * <p>
   * For example:
   *
   * <pre>
   * {@code
   * ChildrenMap.defineOneToOne(
   *     Child.class,
   *     Parent::setChild,
   *     ChildrenMap.matchNested(
   *         Parent::getChild,
   *         Child::getChildId))
   * }
   * </pre>
   *
   * This will match Child to Parent by looking at the Child already stored in the
   * parent and matching the childId.
   *
   * @param <P>          Parent Type
   * @param <C>          Child Type
   * @param <T>          Type that both getters return
   *
   * @param parentGetter Function that receives a parent model and returns the
   *                     child model
   * @param matchGetter  Function that receives a child model and returns the
   *                     identifying value
   * @return A function that runs the getter for parent and the getter for the
   *         pair of children and returns true if the results are equal.
   */
  public static <P, C, T> BiPredicate<P, C> matchNested(
      Function<P, C> parentGetter,
      Function<C, T> matchGetter) {
    return (P parent, C child) -> Optional.ofNullable(parentGetter.apply(parent))
        .map(descendent -> matchGetter.apply(descendent))
        .equals(Optional.ofNullable(matchGetter.apply(child)));
  }

  /**
   * Get a lamdba that tests if a child belongs to a parent based on a field in
   * each.
   *
   * <p>
   * For example:
   *
   * <pre>
   * {@code
   * ChildrenMap.defineOneToOne(
   *     Child.class,
   *     Parent::setChild,
   *     ChildrenMap.matchSimple(
   *         Parent::getChildId,
   *         Child::getFirstChildId))
   * }
   * </pre>
   *
   * This will match Child to Parent by looking at the firstChildId in the parent
   * and matching the childId in Child.
   *
   * @param <P>          Parent Type
   * @param <C>          Child Type
   * @param <T>          Type that both getters return
   *
   * @param parentGetter Function that receives a parent model and returns the
   *                     identifying value
   * @param childGetter  Function that receives a child model and returns the
   *                     identifying value
   * @return A function that runs the getters for parent and child and returns
   *         true if the results are equal.
   */
  public static <P, C, T> BiPredicate<P, C> matchSimple(
      Function<P, T> parentGetter, Function<C, T> childGetter) {
    return (P parent, C child) -> Optional.ofNullable(parentGetter.apply(parent))
        .equals(Optional.ofNullable(childGetter.apply(child)));
  }

  /**
   * Define a parent-child relationship where the children are stored in a
   * {@link List}
   *
   * @param childClass      The class the children should match
   * @param setChildrenList Getter function used to get the list which children
   *                        are added to
   * @param childBelongs    A function that returns true if the given child
   *                        belongs on the given model
   * @return
   */
  public <C> ChildrenMap<P> defineList(
      Class<C> childClass,
      BiConsumer<P, List<C>> setChildrenList,
      BiPredicate<P, C> childBelongs) {
    BiConsumer<P, List<C>> setChildren = (model, children) -> setChildrenList.accept(model, children);
    return define(childClass, setChildren, childBelongs);
  }

  /**
   * Define a parent-child relationship where the children are stored in a
   * {@link Map}
   *
   * @param childClass     The class the children should match
   * @param setChildrenMap Getter function used to get the map which children are
   *                       added to
   * @param getChildKey    A function that provides the key in the map which a
   *                       child should be stored
   *                       in
   * @param childBelongs   A function that returns true if the given child belongs
   *                       on the given model
   * @return
   */
  public <C, K> ChildrenMap<P> defineMap(
      Class<C> childClass,
      BiConsumer<P, Map<K, C>> setChildrenMap,
      Function<C, K> getChildKey,
      BiPredicate<P, C> childBelongs) {
    BiConsumer<P, List<C>> setChildren = (model, children) -> setChildrenMap.accept(model,
        children.stream().collect(Collectors.toMap(getChildKey, c -> c)));
    return define(childClass, setChildren, childBelongs);
  }

  /**
   * Define a parent-child relationship where the children are stored in Lists
   * grouped by a {@link
   * Map}
   *
   * @param childClass     The class the children should match
   * @param setChildrenMap Getter function used to get the map which children are
   *                       added to
   * @param getChildKey    A function that provides the key in the map which a
   *                       child should be stored
   *                       in
   * @param childBelongs   A function that returns true if the given child belongs
   *                       on the given model
   * @return
   */
  public <C, K> ChildrenMap<P> defineMapofLists(
      Class<C> childClass,
      BiConsumer<P, Map<K, List<C>>> setChildrenMap,
      Function<C, K> getChildKey,
      BiPredicate<P, C> childBelongs) {
    // BiConsumer<P, List<C>> setChildren = (model, children) ->
    // setChildrenMap.accept(model,
    // children.toMapOfLists(getChildKey));
    BiConsumer<P, List<C>> setChildren = (model, children) -> setChildrenMap.accept(model,
        children.stream().collect(Collectors.groupingBy(getChildKey)));
    return define(childClass, setChildren, childBelongs);
  }

  /**
   * Define a parent-child relationship where a child is stored in a field on the
   * model
   *
   * @param childClass   The class the child should match
   * @param setChild     Setter function used to set the child
   * @param childBelongs A function that returns true if the given child belongs
   *                     on the given model
   * @return
   */
  public <C> ChildrenMap<P> defineOneToOne(
      Class<C> childClass, BiConsumer<P, C> setChild, BiPredicate<P, C> childBelongs) {
    BiConsumer<P, List<C>> setChildren = (model, children) -> children.stream().findFirst()
        .ifPresent(child -> setChild.accept(model, child));
    ChildMapper<C> definition = new ChildMapper<>(childClass, setChildren, childBelongs);
    childMappers.add(definition);
    addClassToSet(childClass);
    return this;
  }

  /**
   * Define a custom parent-child relationship where the children are stored in
   * the model
   *
   * @param childClass   The class the child should match
   * @param setChildren  Setter function used to set the child
   * @param childBelongs A function that returns true if the given child belongs
   *                     on the given model
   * @return
   */
  public <C> ChildrenMap<P> define(
      Class<C> childClass, BiConsumer<P, List<C>> setChildren, BiPredicate<P, C> childBelongs) {
    ChildMapper<C> definition = new ChildMapper<>(childClass, setChildren, childBelongs);
    childMappers.add(definition);
    addClassToSet(childClass);
    return this;
  }

  private void addClassToSet(Class<?> clazz) {
    if (classesMapped.contains(clazz)) {
      hasMultipleOfSameClass = true;
    } else {
      classesMapped.add(clazz);
    }
  }

  /**
   * Maps the given children to the given parent as appropriate defined by the
   * {@code define} methods called on this.
   *
   * @param model    the parent model to map children to
   * @param children the children to attempt mapping
   */
  public void mapChildren(P model, Object... children) {
    Stream.of(children).filter(Objects::nonNull).forEach(
        child -> {
          boolean mapped = false;
          for (var childMapper : childMappers) {
            if (child instanceof List<?>) {
              mapped = childMapper.attemptManyToOne(model, (List<?>) child);
            } else {
              mapped = childMapper.attemptOneToOne(model, child);
            }

            if (mapped && !hasMultipleOfSameClass) {
              break;
            }
          }
        });
  }

  /**
   * Maps the given children Lists to the corresponding parents.
   *
   * <p>
   * Each child List is iterated over and the individual child is mapped to
   * any parent they belong to
   * by the rules defined using the {@code define} methods.
   *
   * @param inputs   the parent models to map children to
   * @param children the Lists of children to map
   */
  public void mapChildrenAll(List<P> inputs, List<?>... children) {
    final List<List<?>> filteredChildren = Stream.of(children).filter(Objects::nonNull).toList();

    filteredChildren.forEach(
        child -> {
          for (var childMapper : childMappers) {
            if (childMapper.attemptManyToMany(inputs, child) && !hasMultipleOfSameClass) {
              break;
            }
          }
        });
  }

  class ChildMapper<C> {
    private final Class<C> childClass;
    private final BiConsumer<P, List<C>> setChildren;
    private final BiPredicate<P, C> childBelongs;

    public ChildMapper(
        Class<C> childClass,
        BiConsumer<P, List<C>> setChildren,
        BiPredicate<P, C> childBelongs) {
      this.childClass = childClass;
      this.setChildren = setChildren;
      this.childBelongs = childBelongs;
    }

    public boolean attemptOneToOne(P parent, Object potentialChild) {
      return cast(potentialChild)
          .filter(child -> childBelongs.test(parent, child))
          .map(setter(parent))
          .orElse(false);
    }

    public boolean attemptManyToOne(P parent, List<?> list) {
      List<C> children = castList(list).filter(c -> childBelongs.test(parent, c)).toList();

      if (!children.isEmpty()) {
        setChildren.accept(parent, children);
        return true;
      }

      return false;
    }

    public boolean attemptManyToMany(List<P> parents, List<?> list) {
      List<Tuple<P, List<C>>> joined = parents.stream().map(p -> new Tuple<>(p,
          castList(list)
              .filter(c -> childBelongs.test(p, c))
              .toList()))
          .filter(t -> !t.getRight().isEmpty()).toList();
      joined.forEach(group -> setChildren.accept(group.getLeft(), group.getRight()));

      return !joined.isEmpty();
    }

    private Function<C, Boolean> setter(P parent) {
      return c -> {
        setChildren.accept(parent, List.of(c));
        return true;
      };
    }

    @SuppressWarnings("unchecked")
    private Stream<C> castList(List<?> input) {
      if (!input.isEmpty()
          && input.get(0) != null
          && childClass.isAssignableFrom(input.get(0).getClass())) {
        return ((List<C>) input).stream();
      } else if (!input.isEmpty() && input.get(0) == null) {
        throw new IllegalArgumentException(
            "ChildrenMap can't work with lists that start with a null element");
      }
      return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<C> cast(Object input) {
      if (childClass.isAssignableFrom(input.getClass())) {
        return Optional.ofNullable((C) input);
      }
      return Optional.empty();
    }
  }
}
