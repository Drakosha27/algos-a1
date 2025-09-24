package org.example;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        int[] a = {5,3,8,4,2,7,1,0,9,6,6,4,2};
        System.out.println("До:      " + Arrays.toString(a));

        // QuickSort:
        QuickSort.Stats st = QuickSort.sort(a);

        System.out.println("После:   " + Arrays.toString(a));
        System.out.println("OK?      " + QuickSort.isSorted(a));
        System.out.println("Статистика: " + st + ", cut-off=" + QuickSort.INSERTION_SORT_CUTOFF);
    }
}
