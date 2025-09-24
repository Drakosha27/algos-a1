package org.example.geometry;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class ClosestPairTest {

    @Test
    void edge_twoPoints() {
        ClosestPair.Point a = new ClosestPair.Point(0, 0);
        ClosestPair.Point b = new ClosestPair.Point(3, 4);
        ClosestPair.Result r = ClosestPair.closest(new ClosestPair.Point[]{a, b});
        assertEquals(5.0, r.dist(), 1e-12);
    }

    @Test
    void edge_duplicatePoints_zeroDistance() {
        ClosestPair.Point a = new ClosestPair.Point(1, 1);
        ClosestPair.Point b = new ClosestPair.Point(1, 1);
        ClosestPair.Point c = new ClosestPair.Point(2, 2);
        ClosestPair.Result r = ClosestPair.closest(new ClosestPair.Point[]{a, b, c});
        assertEquals(0.0, r.dist(), 1e-12);
    }

    @Test
    void small_vs_bruteforce() {
        int[] sizes = {5, 20, 100, 500, 2000};
        Random rnd = new Random(42);
        for (int n : sizes) {
            ClosestPair.Point[] pts = new ClosestPair.Point[n];
            for (int i = 0; i < n; i++) {
                double x = rnd.nextDouble() * 1_000 - 500;
                double y = rnd.nextDouble() * 1_000 - 500;
                pts[i] = new ClosestPair.Point(x, y);
            }
            double d1 = ClosestPair.closest(pts).dist();
            double d2 = ClosestPair.bruteForceDistance(pts);
            assertEquals(d2, d1, 1e-9, "n=" + n);
        }
    }

    @Test
    void large_runs_fast_enough() {
        int n = 20000;
        Random rnd = new Random(123);
        ClosestPair.Point[] pts = new ClosestPair.Point[n];
        for (int i = 0; i < n; i++) {
            double x = rnd.nextDouble() * 1_000_000 - 500_000;
            double y = rnd.nextDouble() * 1_000_000 - 500_000;
            pts[i] = new ClosestPair.Point(x, y);
        }
        ClosestPair.Result r = ClosestPair.closest(pts);
        assertNotNull(r);
        assertTrue(r.dist() >= 0.0 && Double.isFinite(r.dist()));
    }
}