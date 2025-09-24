package org.example.geometry;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Классическое решение O(n log n):
 * 1) сортируем по x;
 * 2) рекурсия с объединением списков по y;
 * 3) в "полосе" проверяем до 7 соседей по y.
 */
public class ClosestPair {

    public record Point(double x, double y) {}
    public record Result(Point a, Point b, double dist) {
        @Override public String toString() {
            return "d=" + dist + ", a=(" + a.x + "," + a.y + "), b=(" + b.x + "," + b.y + ")";
        }
    }

    public static Result closest(Point[] pts) {
        if (pts == null || pts.length < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }
        Point[] px = pts.clone();
        Arrays.sort(px, Comparator.comparingDouble(p -> p.x)); // по X
        Point[] byY = new Point[px.length];
        Point[] buf = new Point[px.length];
        return rec(px, 0, px.length, byY, buf);
    }

    private static Result rec(Point[] px, int lo, int hi, Point[] byY, Point[] buf) {
        int n = hi - lo;
        if (n <= 3) {
            Result r = brute(px, lo, hi);
            Arrays.sort(px, lo, hi, Comparator.comparingDouble(p -> p.y));
            System.arraycopy(px, lo, byY, lo, n);
            return r;
        }

        int mid = (lo + hi) >>> 1;
        double midX = px[mid].x;

        Result left  = rec(px, lo, mid, byY, buf);
        Result right = rec(px, mid, hi, byY, buf);
        Result best  = left.dist <= right.dist ? left : right;

        // merge byY по Y: [lo..mid) и [mid..hi)
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) {
            if (byY[i].y <= byY[j].y) buf[k++] = byY[i++];
            else                       buf[k++] = byY[j++];
        }
        while (i < mid) buf[k++] = byY[i++];
        while (j < hi)  buf[k++] = byY[j++];
        System.arraycopy(buf, lo, byY, lo, hi - lo);

        // "полоса" шириной best.dist по X около midX
        int m = 0;
        for (int t = lo; t < hi; t++) {
            if (Math.abs(byY[t].x - midX) < best.dist) {
                buf[m++] = byY[t];
            }
        }

        // проверяем до 7 соседей по Y
        for (int a = 0; a < m; a++) {
            for (int b = a + 1; b < m && (b - a) <= 7; b++) {
                double d = dist(buf[a], buf[b]);
                if (d < best.dist) best = new Result(buf[a], buf[b], d);
            }
        }
        return best;
    }

    private static Result brute(Point[] px, int lo, int hi) {
        double best = Double.POSITIVE_INFINITY;
        Point pa = null, pb = null;
        for (int i = lo; i < hi; i++) {
            for (int j = i + 1; j < hi; j++) {
                double d = dist(px[i], px[j]);
                if (d < best) { best = d; pa = px[i]; pb = px[j]; }
            }
        }
        return new Result(pa, pb, best);
    }

    public static double bruteForceDistance(Point[] pts) {
        if (pts.length < 2) return Double.POSITIVE_INFINITY;
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < pts.length; i++) {
            for (int j = i + 1; j < pts.length; j++) {
                best = Math.min(best, dist(pts[i], pts[j]));
            }
        }
        return best;
    }

    private static double dist(Point a, Point b) {
        double dx = a.x - b.x, dy = a.y - b.y;
        return Math.hypot(dx, dy);
    }
}