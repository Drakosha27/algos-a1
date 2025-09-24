package org.example;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {

    @Test
    void smallFixed() {
        int[] a = {5, 3, 8, 4, 2, 7, 1, 0, 9, 6, 6, 4, 2};
        QuickSort.sort(a);
        assertTrue(QuickSort.isSorted(a));
    }

    @Test
    void randomMany() {
        Random rnd = new Random(1);
        for (int t = 0; t < 50; t++) {
            int n = 1 + rnd.nextInt(500);
            int[] a = rnd.ints(n, -1000, 1000).toArray();
            int[] b = Arrays.copyOf(a, a.length);
            Arrays.sort(b);
            QuickSort.sort(a);
            assertArrayEquals(b, a);
        }
    }
}
