package com.lbisoft.core.framework.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Makes a comparison using multiple comparators in order.
 * Returns the result of the first comparator not to return a tie.
 * @param <T> Comparator type
 */
public class MultiComparator<T> implements Comparator<T> {
    
    private final List<Comparator<T>> comparators;

    public MultiComparator(List<Comparator<T>> comparators) {
        this.comparators = comparators;
    }

    @SafeVarargs
    public MultiComparator(Comparator<T>... comparators) {
        this(Arrays.asList(comparators));
    }
    
    /**
     * Returns the result of the first comparator not to return a tie.
     */
    public int compare(T o1, T o2) {
        for (Comparator<T> c : comparators) {
            int result = c.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @SafeVarargs
    public static <T> void sort(List<T> list, Comparator<T>... comparators) {
        list.sort(new MultiComparator<T>(comparators));
    }

}
