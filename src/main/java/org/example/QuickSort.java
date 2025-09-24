package org.example;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public final class QuickSort {

    private QuickSort() {}

    public static int INSERTION_SORT_CUTOFF = 8;

    public static final class Stats {
        public long cmp, swap, partitions, inserts;
        @Override public String toString() {
            return "cmp=" + cmp + ", swap=" + swap + ", partitions=" + partitions + ", inserts=" + inserts;
        }
    }

    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }

    public static Stats sort(int[] a) {
        Stats st = new Stats();
        if (a.length > 1) quicksort(a, 0, a.length - 1, st);
        return st;
    }

    // Хвостовая оптимизация: рекурсируем только в меньший отрезок, больший — через while
    private static void quicksort(int[] a, int lo, int hi, Stats st) {
        while (lo < hi) {
            // Маленькие сегменты — вставками
            if (hi - lo + 1 <= INSERTION_SORT_CUTOFF) {
                insertionSort(a, lo, hi, st);
                st.inserts++;
                return;
            }

            // Случайный pivot
            int p = ThreadLocalRandom.current().nextInt(lo, hi + 1);
            swap(a, lo, p, st); // pivot в a[lo]

            // Hoare partitioning
            int i = lo - 1, j = hi + 1;
            int pivot = a[lo];
            while (true) {
                do { i++; st.cmp++; } while (a[i] < pivot);
                do { j--; st.cmp++; } while (a[j] > pivot);
                if (i >= j) { st.partitions++; break; }
                swap(a, i, j, st);
            }

            // Теперь [lo..j] <= pivot <= [j+1..hi]
            int leftLo = lo, leftHi = j;
            int rightLo = j + 1, rightHi = hi;

            // Рекурсируем только в меньший кусок
            if (leftHi - leftLo < rightHi - rightLo) {
                if (leftLo < leftHi) quicksort(a, leftLo, leftHi, st);
                lo = rightLo; // «хвост» — продолжаем циклом
                hi = rightHi;
            } else {
                if (rightLo < rightHi) quicksort(a, rightLo, rightHi, st);
                lo = leftLo;
                hi = leftHi;
            }
        }
    }

    private static void insertionSort(int[] a, int lo, int hi, Stats st) {
        for (int i = lo + 1; i <= hi; i++) {
            int x = a[i];
            int j = i - 1;
            while (j >= lo) {
                st.cmp++;
                if (a[j] <= x) break;
                a[j + 1] = a[j];
                st.swap++;          // считаем как «перемещение»
                j--;
            }
            a[j + 1] = x;
        }
    }

    private static void swap(int[] a, int i, int j, Stats st) {
        if (i == j) return;
        int t = a[i]; a[i] = a[j]; a[j] = t;
        st.swap++;
    }

    // Быстрая проверка/демо
    public static void main(String[] args) {
        int[] a = {5,3,8,4,2,7,1,0,9,6,6,4,2};
        System.out.println("До:    " + Arrays.toString(a));
        Stats st = sort(a);
        System.out.println("После: " + Arrays.toString(a));
        System.out.println("OK?    " + isSorted(a));
        System.out.println("Статистика: " + st + ", cut-off=" + INSERTION_SORT_CUTOFF);
    }
}
