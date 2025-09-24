package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SelectTest {

    @Test
    void randomMatchesSort() {
        Random r = new Random(123);
        for (int t = 0; t < 100; t++){
            int n = 1 + r.nextInt(200);
            int[] a = new int[n];
            for (int i=0;i<n;i++) a[i] = r.nextInt(1000) - 500;
            int[] b = Arrays.copyOf(a, n);
            Arrays.sort(b);
            int k = r.nextInt(n);
            int v = Select.select(a, k);
            assertEquals(b[k], v);
        }
    }

    @Test
    void edgesAndDuplicates(){
        int[] a = {5,5,5,1,9,9,2,2,2,2,7};
        int[] b = Arrays.copyOf(a, a.length);
        Arrays.sort(b);
        assertEquals(b[0], Select.select(Arrays.copyOf(a, a.length), 0));
        assertEquals(b[b.length-1], Select.select(Arrays.copyOf(a, a.length), b.length-1));
        assertEquals(b[b.length/2], Select.select(Arrays.copyOf(a, a.length), b.length/2));
        assertEquals(b[3], Select.select(Arrays.copyOf(a, a.length), 3));
    }
}