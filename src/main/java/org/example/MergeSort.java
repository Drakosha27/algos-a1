package org.example;

import java.util.Arrays;

public class MergeSort {

    /** Порог, при котором вместо merge-sort используем вставки */
    public static final int INSERTION_SORT_CUTOFF = 8;

    /** Сбор статистики */
    public static class Stats {
        public long comparisons;
        public long copies;
        public long merges;
        public long insertions;

        @Override public String toString() {
            return "cmp=" + comparisons +
                    ", copy=" + copies +
                    ", merges=" + merges +
                    ", inserts=" + insertions;
        }
    }

    /** Публичная точка входа */
    public static Stats sort(int[] a) {
        Stats s = new Stats();
        if (a == null || a.length < 2) return s;
        int[] aux = Arrays.copyOf(a, a.length);
        sort(a, aux, 0, a.length - 1, s);
        return s;
    }

    // ----- внутренности сортировки -----

    private static void sort(int[] a, int[] aux, int lo, int hi, Stats s) {
        if (hi - lo + 1 <= INSERTION_SORT_CUTOFF) {
            insertion(a, lo, hi, s);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, s);
        sort(a, aux, mid + 1, hi, s);

        // Оптимизация: если границы уже в порядке — не мержим
        s.comparisons++;
        if (a[mid] <= a[mid + 1]) return;

        merge(a, aux, lo, mid, hi, s);
    }

    /** Стабильная сортировка вставками на участке [lo..hi] */
    private static void insertion(int[] a, int lo, int hi, Stats s) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= lo) {
                s.comparisons++;
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    s.copies++;
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
            s.copies++;
            s.insertions++;
        }
    }

    /** Слияние двух отсортированных половин [lo..mid] и [mid+1..hi] */
    private static void merge(int[] a, int[] aux, int lo, int mid, int hi, Stats s) {
        for (int k = lo; k <= hi; k++) { aux[k] = a[k]; s.copies++; }

        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++]; s.copies++;
            } else if (j > hi) {
                a[k] = aux[i++]; s.copies++;
            } else {
                s.comparisons++;
                if (aux[j] < aux[i]) {
                    a[k] = aux[j++]; s.copies++;
                } else {
                    a[k] = aux[i++]; s.copies++;
                }
            }
        }
        s.merges++;
    }

    /** Проверка отсортированности */
    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i] < a[i - 1]) return false;
        return true;
    }
}
