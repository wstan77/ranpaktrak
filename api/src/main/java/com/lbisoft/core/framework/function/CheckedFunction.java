package com.lbisoft.core.framework.function;

@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable> {
  R apply(T t) throws E;
}