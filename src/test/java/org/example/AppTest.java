package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    void testSortingWorks() {
        int[] a = {5, 3, 8, 1, 2};
        MergeSort.Stats stats = MergeSort.sort(a);
        assertTrue(MergeSort.isSorted(a), "Массив должен быть отсортирован");
        System.out.println("Статистика: " + stats);
    }
}
