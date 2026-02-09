package com.lbisoft.core.framework.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.lbisoft.core.framework.exceptions.ConcurrentExceptions;

public class ParallelUtils {

  private static ExecutorService executor;

  public static ExecutorService getExecutor() {
    if (executor == null) {
      executor = Executors.newFixedThreadPool(16);
    }
    return executor;
  }

  @SafeVarargs
  public static <T> List<T> runInParallel(Supplier<T>... functions) throws ConcurrentExceptions {
    List<CompletableFuture<T>> futures = Arrays.stream(functions)
        .filter(func -> func != null)
        .map(func -> CompletableFuture.supplyAsync(func, getExecutor()))
        .collect(Collectors.toList());

    List<T> results = new ArrayList<>();
    List<Throwable> exceptions = new ArrayList<>();

    for (CompletableFuture<T> future : futures) {
      try {
        results.add(future.join());
      } catch (CompletionException ce) {
        exceptions.add(ce.getCause());
      }
    }

    if (!exceptions.isEmpty()) {
      throw new ConcurrentExceptions("Concurrent tasks failed.", exceptions);
    }

    return results;
  }

}
