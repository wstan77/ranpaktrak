package com.lbisoft.core.framework.provider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.lbisoft.core.framework.exceptions.ConcurrentExceptions;
import com.lbisoft.core.framework.models.Tuple;
import com.lbisoft.core.framework.utils.ParallelUtils;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

/**
 * AbstractProvider is an opinionated base class for simplifiying the creation
 * of "providers" which convert between two specific model classes.
 *
 * <p>
 * The primary use of AbstractProvider is to {@code map} an instance of class
 * {@code <A>} to a new instance of class {@code <B>}.
 *
 * <p>
 * By default, AbstractProvider will automatically map values between fields if:
 *
 * <p>
 * <ul>
 * <li>Both classes have a field with exactly the same name
 * <li>Both of those fields have a compatible type
 * <li>There isn't a manually defined mapping covering the field
 * </ul>
 *
 * <p>
 * Fields can be configured manually to map by using
 * {@link ClassMapBuilder#field(String, String)} in the implementation of
 * {@link #defineClassMap(ClassMapBuilder)}.
 *
 * <p>
 * AbstractMapper also leverages {@link ChildrenMap} to facilitate mapping a
 * list of children models to the corresponding parent model. Configure the
 * mapping of children to parent in the implementation of
 * {@link #defineChildren(ChildrenMap)}. Only class {@code <B>} acts as a
 * parent, reverse mapping doesn't support children.
 *
 * @param <A> Input class
 * @param <B> Output class
 */
public abstract class AbstractProvider<A, B> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProvider.class);

  protected final BoundMapperFacade<A, B> mapper;
  public ChildrenMap<B> childrenMap;

  /**
   * Creates a new provider. Recommended to use this constructor and not extend it
   * in implementation.
   */
  @SuppressWarnings("unchecked")
  public AbstractProvider(Class<A> classA, Class<B> classB) {
    MapperFactory mapperFactory = GblMapperFactory.getMapperFactory();

    if (classA == null || classB == null) {
      Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
      classA = (Class<A>) types[0];
      classB = (Class<B>) types[1];
    }

    ClassMapBuilder<A, B> classMap = mapperFactory.classMap(classA, classB).mapNulls(false).mapNullsInReverse(false);

    defineClassMap(classMap);

    classMap.byDefault().register();

    childrenMap = new ChildrenMap<>();
    defineChildren(childrenMap);

    mapper = mapperFactory.getMapperFacade(classA, classB);
  }

  public AbstractProvider() {
    this(null, null);
  }

  /**
   * Method to define the relationship between class {@code <A>} and {@code <B>}.
   * Use this method to set up custom field mapping.
   *
   * <p>
   * If all fields are handled by automatic mapping, leave the method body empty
   *
   * @param classMapBuilder ClassMapBuilder used by AbstractProvider, already
   *                        filled in with automatic mappings
   */
  protected abstract void defineClassMap(ClassMapBuilder<A, B> classMapBuilder);

  /**
   * Method to define the relationship between class {@code <B>} and any nested
   * models. Use this method to set up parent-child mapping.
   *
   * <p>
   * If class {@code <B>} has no children models, leave the method body empty
   *
   * @param classMapBuilder ClassMapBuilder used by AbstractProvider, already
   *                        filled in with automatic mappings
   */
  protected abstract void defineChildren(ChildrenMap<B> childrenMap);

  /**
   * Map an instance of class {@code <A>} to a new instance of class {@code <B>}.
   *
   * <p>
   * Any additional parameters will be passed along to the {@link ChildrenMap}
   * configured in {@link #defineChildren(ChildrenMap)}, which will map children
   * as appropriate to the mapped instance.
   *
   * @param input    Instance to map.
   * @param children Children to attempt to map to the output.
   * @return New instance of {@code <B>} mapped from the input.
   */
  public final B map(A input, Object... children) {
    if (input == null)
      return null;

    B output = mapper.map(input);
    output = prepareToMapChildren(input, output, children);
    addChildren(output, children);

    return completeMap(input, output, children);
  }

  /**
   * Map a {@link List} of class {@code <A>} to a {@link List} of new
   * instances of class {@code <B>}.
   *
   * <p>
   * Any additional parameters will be passed along to the {@link ChildrenMap}
   * configured in {@link #defineChildren(ChildrenMap)}, which will map children
   * as appropriate to the mapped instances.
   *
   * <p>
   * Provides performance benefits in mapping children over calling
   * {@link #map(Object, Object...)} in a loop.
   *
   * @param inputs   {@link List} of instances to map.
   * @param children Children to attempt to map to the output.
   * @return {@link List} of new instances of {@code <B>} mapped from the
   *         input.
   */
  public final List<B> mapAll(List<? extends A> inputs, List<?>... children) {
    var values = inputs.stream().filter(Objects::nonNull).map(input -> {
      B output = mapper.map(input);
      return new Tuple<>(input, prepareToMapChildrenAll(input, output, children));
    }).toList();
    childrenMap.mapChildrenAll(values.stream().map(Tuple::getRight).toList(), children);

    return values.stream().map(tuple -> completeMapAll(tuple.getLeft(), tuple.getRight(), children)).toList();
  }

  /**
   * WARNING: Parallel calls do NOT read uncommitted data, avoid using this
   * inside of a INSERT/UPDATE/DELETE transaction.
   *
   * <p>
   * Map a {@link List} of class {@code <A>} to a {@link List} of new
   * instances of class {@code <B>}, fetching children in parallel.
   *
   * <p>
   * Supplier functions will be run in parallel, and the results will be passed
   * along to {@link #mapAll(List, List...)}.
   *
   * @param inputs          {@link List} of instances to map.
   * @param childrenGetters Supplier functions to fetch children.
   * @return {@link List} of new instances of {@code <B>} mapped from the
   *         input.
   */
  @SafeVarargs
  public final List<B> mapParallel(List<? extends A> inputs, Supplier<List<?>>... childrenGetters) {
    if (TransactionSynchronizationManager.isActualTransactionActive()
        && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
      LOGGER.warn(
          "WARNING Parallel calls do NOT read uncommitted data, avoid using mapParallel outside of read-only transactions.");
    }

    try {
      List<?>[] children = ParallelUtils.runInParallel(childrenGetters).toArray(new List<?>[0]);
      return mapAll(inputs, children);
    } catch (ConcurrentExceptions e) {
      // SLF4J doesn't handle the custom stack trace printing, so we need to generate
      // a string
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      LOGGER.error("Failed to map children in parallel:\n" + sw.toString());
      return Collections.emptyList();
    }
  }

  /**
   * Adds children to an existing instance of {@code <B>}.
   *
   * <p>
   * Useful only if you already have an instance of {@code <B>}, otherwise use
   * {@link #map(Object, Object...)}
   *
   * @param parent   Parent model to map children to.
   * @param children Children to attempt to map to the model.
   * @return Parent model with children mapped as appropriate.
   */
  public final B addChildren(B parent, Object... children) {
    childrenMap.mapChildren(parent, children);
    return parent;
  }

  /**
   * Map an instance of class {@code <B>} to a new instance of class {@code <B>}.
   *
   * <p>
   * Children mapping isn't supported in reverse mapping.
   *
   * @param input Instance to map.
   * @return New instance of {@code <A>} mapped from the input.
   */
  public final A reverseMap(B input) {
    if (input == null)
      return null;

    A output = mapper.mapReverse(input);

    return completeReverseMap(input, output);
  }

  /**
   * Map a {@link List} of class {@code <B>} to a {@link List} of new
   * instances of class {@code <B>}.
   *
   * <p>
   * Simply for convenience.
   *
   * @param models {@link List} of instances to map.
   * @return {@link List} of new instances of {@code <A>} mapped from the
   *         input.
   */
  public final List<A> reverseMapAll(List<? extends B> models) {
    return models.stream().map(this::reverseMap).toList();
  }

  /**
   * Override this method to take any steps between when an instance of
   * {@code <A>} is mapped to an instance of {@code <B>} and when the mapped
   * output is passed to {@link ChildrenMap}. This is a good opportunity to do
   * manual transformations required by the children mapping.
   *
   * @param input    Input to be mapped
   * @param output   Partially mapped output
   * @param children Children to be mapped
   * @return The prepared output
   */
  protected B prepareToMapChildren(A input, B output, Object... children) {
    return output;
  }

  /**
   * Override this method to take any steps between when a {@link List} of
   * {@code <A>} is mapped to a {@link List} of {@code <B>} and when the
   * mapped output is passed to {@link ChildrenMap}. This is a good opportunity to
   * do manual transformations required by the children mapping.
   *
   * @param input    Input to be mapped
   * @param output   Partially mapped output
   * @param children Children to be mapped
   * @return The prepared output
   */
  protected B prepareToMapChildrenAll(A input, B output, List<?>... children) {
    return output;
  }

  /**
   * Override this method to take any steps after an instance of {@code <A>} is
   * mapped to an instance of {@code <B>} and the children are mapped.
   *
   * <p>
   * This allows you to do any manual transformations to the output of the
   * provider and perform manual children mapping.
   *
   * @param input    Input to be mapped
   * @param output   Mapped output
   * @param children Children mapped
   * @return The completed output
   */
  protected B completeMap(A input, B output, Object... children) {
    return output;
  }

  /**
   * Override this method to take any steps after a {@link List} of
   * {@code <A>} is mapped to a a {@link List} of {@code <B>} and the children
   * are mapped.
   *
   * <p>
   * This allows you to do any manual transformations to the output of the
   * provider and perform manual children mapping.
   *
   * @param input    Input to be mapped
   * @param output   Mapped output
   * @param children Children mapped
   * @return The completed output
   */
  protected B completeMapAll(A input, B output, List<?>... children) {
    return output;
  }

  /**
   * Override this method to take any steps after an instance of {@code <B>} is
   * mapped to an instance of {@code <A>}.
   *
   * <p>
   * <p>
   * This allows you to do any manual transformations to the output of the
   * provider.
   *
   * @param input  Input to be mapped
   * @param output Mapped output
   * @return The completed output
   */
  protected A completeReverseMap(B input, A output) {
    return output;
  }
}
