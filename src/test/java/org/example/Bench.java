package org.example;

import java.util.Arrays;
import java.util.Random;

public class Bench {

    static int[] make(int n, long seed){
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(1_000_000) - 500_000;
        return a;
    }

    static void runOne(String name, int[] src){
        int[] a = Arrays.copyOf(src, src.length);
        MergeSort.Stats st = MergeSort.sort(a);
        System.out.printf("%-8s n=%-7d %s ok=%s%n",
                name, a.length, st, MergeSort.isSorted(a));
    }

    public static void main(String[] args) {
        int[] sizes = {10, 100, 1_000, 10_000, 100_000};
        long seed = 42;
        for (int n : sizes) {
            int[] src = make(n, seed);
            runOne("merge", src);
        }
    }
}
