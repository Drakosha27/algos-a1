package org.example;

import java.util.Random;

public class Select {

    // Публичный API: вернуть k-й статистический порядок (0..n-1). Массив может быть переупорядочен.
    public static int select(int[] a, int k){
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of range");
        return selectRange(a, 0, a.length, k);
    }

    // Выбор в полуинтервале [lo, hi)
    private static int selectRange(int[] a, int lo, int hi, int k){
        while (true) {
            int n = hi - lo;
            if (n <= 10) {              // маленькие случаи - insertion sort и вернуть k-й
                insertionSort(a, lo, hi);
                return a[lo + k];
            }

            int pivotIndex = medianOfMediansIndex(a, lo, hi);
            int p = partition(a, lo, hi, pivotIndex);

            int leftSize = p - lo;
            if (k < leftSize) {
                // искомый в левой части
                hi = p;
            } else if (k > leftSize) {
                // искомый в правой части
                k -= (leftSize + 1);
                lo = p + 1;
            } else {
                return a[p]; // ровно опорный и есть ответ
            }
        }
    }

    // In-place partition вокруг a[pivotIndex], возвращает итоговый индекс опорного
    private static int partition(int[] a, int lo, int hi, int pivotIndex){
        int pivot = a[pivotIndex];
        swap(a, pivotIndex, hi - 1);
        int store = lo;
        for (int i = lo; i < hi - 1; i++){
            if (a[i] < pivot){
                swap(a, i, store++);
            }
        }
        swap(a, store, hi - 1);
        return store;
    }

    private static void swap(int[] a, int i, int j){
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    private static void insertionSort(int[] a, int lo, int hi){
        for (int i = lo + 1; i < hi; i++){
            int x = a[i], j = i - 1;
            while (j >= lo && a[j] > x){
                a[j+1] = a[j];
                j--;
            }
            a[j+1] = x;
        }
    }

    // --- Median-of-Medians (группы по 5) ---

    private static int medianOfMediansIndex(int[] a, int lo, int hi){
        int n = hi - lo;
        int groups = (n + 4) / 5;
        // Сжимаем медианы групп в начало диапазона
        for (int g = 0; g < groups; g++){
            int gLo = lo + g*5;
            int gHi = Math.min(gLo + 5, hi);
            int m = medianIndexOfSmallBlock(a, gLo, gHi);
            swap(a, lo + g, m);
        }
        // Рекурсивно находим медиану среди этих groups элементов
        int mid = groups / 2;
        return selectIndexAmongPrefix(a, lo, lo + groups, mid);
    }

    // Возвращает индекс k-го элемента среди префикса [lo, hi)
    private static int selectIndexAmongPrefix(int[] a, int lo, int hi, int k){
        int resVal = selectRange(a, lo, hi, k);
        for (int i = lo; i < hi; i++){
            if (a[i] == resVal) return i;
        }
        return lo + k; // теоретически не должно понадобиться
    }

    // Медиана маленького блока size 1..5, возвращает индекс медианы
    private static int medianIndexOfSmallBlock(int[] a, int lo, int hi){
        insertionSort(a, lo, hi);
        int size = hi - lo;
        return lo + size/2;
    }
}