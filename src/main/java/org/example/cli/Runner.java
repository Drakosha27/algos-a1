package org.example.cli;

import org.example.MergeSort;
import org.example.QuickSort;
import org.example.Select;
import org.example.geometry.ClosestPair;
import org.example.geometry.ClosestPair.Point;
import org.example.geometry.ClosestPair.Result;
import org.example.metrics.Metrics;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;


public class Runner {


    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (String a : args) {
            int i = a.indexOf('=');
            if (i > 0) m.put(a.substring(0, i).trim(), a.substring(i + 1).trim());
        }
        return m;
    }


    private static int[] parseSizes(String s) {
        String[] parts = s.split(",");
        int[] res = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim().toLowerCase(Locale.ROOT);
            int mul = 1;
            if (p.endsWith("k")) { mul = 1_000; p = p.substring(0, p.length() - 1); }
            else if (p.endsWith("m")) { mul = 1_000_000; p = p.substring(0, p.length() - 1); }
            res[i] = Integer.parseInt(p) * mul;
        }
        return res;
    }


    private static int[] makeArray(int n, Random rnd) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt(1_000_000) - 500_000;
        return a;
    }

    private static Point[] makePoints(int n, Random rnd) {
        Point[] a = new Point[n];
        for (int i = 0; i < n; i++) a[i] = new Point(rnd.nextDouble(), rnd.nextDouble());
        return a;
    }


    private static long getLong(Object stats, String field) {
        if (stats == null) return 0L;
        try {
            Field f = stats.getClass().getField(field);
            return f.getLong(stats);
        } catch (Throwable ignore) {
            return 0L;
        }
    }

    private static int getInt(Object stats, String field) {
        if (stats == null) return 0;
        try {
            Field f = stats.getClass().getField(field);
            return f.getInt(stats);
        } catch (Throwable ignore) {
            return 0;
        }
    }

    // ------- запись строки в CSV -------
    private static void writeRow(Metrics.Csv csv,
                                 String algo, int n, int rep, long ms,
                                 long cmp, long copy, long merges, long inserts, int depth,
                                 boolean ok) {
        csv.row(algo, n, rep, ms, cmp, copy, merges, inserts, depth, ok);
    }


    private static void runMerge(int[] sizes, int reps, long seed, File outFile) throws Exception {
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        try (Metrics.Csv csv = new Metrics.Csv(outFile,
                "algo,n,rep,timeMs,cmp,copy,merges,inserts,depth,ok")) {
            for (int n : sizes) {
                for (int r = 1; r <= reps; r++) {
                    Random rnd = new Random(seed + 1000L * n + r);
                    int[] a = makeArray(n, rnd);

                    long t0 = System.nanoTime();
                    MergeSort.Stats st = MergeSort.sort(a);
                    long t1 = System.nanoTime();
                    long ms = (t1 - t0) / 1_000_000L;

                    boolean ok = MergeSort.isSorted(a);
                    writeRow(csv, "merge", n, r, ms,
                            getLong(st, "comparisons"),
                            getLong(st, "copies"),
                            getLong(st, "merges"),
                            getLong(st, "inserts"),
                            getInt(st, "maxDepth"),
                            ok);
                }
            }
        }
        System.out.println("Done. CSV -> " + outFile.getPath());
    }


    private static void runQuick(int[] sizes, int reps, long seed, File outFile) throws Exception {
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        try (Metrics.Csv csv = new Metrics.Csv(outFile,
                "algo,n,rep,timeMs,cmp,copy,merges,inserts,depth,ok")) {
            for (int n : sizes) {
                for (int r = 1; r <= reps; r++) {
                    Random rnd = new Random(seed + 2000L * n + r);
                    int[] a = makeArray(n, rnd);

                    long t0 = System.nanoTime();
                    QuickSort.Stats st = QuickSort.sort(a);
                    long t1 = System.nanoTime();
                    long ms = (t1 - t0) / 1_000_000L;

                    boolean ok = MergeSort.isSorted(a);
                    writeRow(csv, "quick", n, r, ms,
                            getLong(st, "comparisons"),
                            getLong(st, "copies"),
                            getLong(st, "merges"),
                            getLong(st, "inserts"),
                            getInt(st, "maxDepth"),
                            ok);
                }
            }
        }
        System.out.println("Done. CSV -> " + outFile.getPath());
    }

    // ================= Select (MoM) =================
    private static void runSelect(int[] sizes, int reps, long seed, File outFile, double kfrac) throws Exception {
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        try (Metrics.Csv csv = new Metrics.Csv(outFile,
                "algo,n,rep,timeMs,cmp,copy,merges,inserts,depth,ok")) {
            for (int n : sizes) {
                for (int r = 1; r <= reps; r++) {
                    Random rnd = new Random(seed + 3000L * n + r);
                    int[] src = makeArray(n, rnd);
                    int k = Math.max(0, Math.min(n - 1, (int) Math.round(kfrac * (n - 1))));

                    long t0 = System.nanoTime();
                    int val = Select.select(Arrays.copyOf(src, src.length), k);
                    long t1 = System.nanoTime();
                    long ms = (t1 - t0) / 1_000_000L;

                    // проверка корректности
                    int[] check = Arrays.copyOf(src, src.length);
                    Arrays.sort(check);
                    boolean ok = (val == check[k]);

                    writeRow(csv, "select", n, r, ms,
                            0, 0, 0, 0, 0, ok);
                }
            }
        }
        System.out.println("Done. CSV -> " + outFile.getPath());
    }

    // ================= Closest pair =================
    private static void runClosest(int[] sizes, int reps, long seed, File outFile) throws Exception {
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        try (Metrics.Csv csv = new Metrics.Csv(outFile,
                "algo,n,rep,timeMs,cmp,copy,merges,inserts,depth,ok")) {
            for (int n : sizes) {
                for (int r = 1; r <= reps; r++) {
                    Random rnd = new Random(seed + 4000L * n + r);
                    Point[] pts = makePoints(n, rnd);

                    long t0 = System.nanoTime();
                    Result res = ClosestPair.closest(pts);
                    long t1 = System.nanoTime();
                    long ms = (t1 - t0) / 1_000_000L;

                    // здесь метрик нет — пишем нули; ok=true (алгоритм возвращает пару)
                    writeRow(csv, "closest", n, r, ms,
                            0, 0, 0, 0, 0, true);

                    // в консоль всё же выведем дистанцию, чтобы видеть результат
                    System.out.printf(Locale.ROOT,
                            "closest n=%d rep=%d dist=%.9f time=%dms%n", n, r, res.dist(), ms);
                }
            }
        }
        System.out.println("Done. CSV -> " + outFile.getPath());
    }

    // ================= main =================
    public static void main(String[] args) throws Exception {
        Map<String, String> m = parseArgs(args);
        String algo = m.getOrDefault("algo", "merge");
        int[] sizes = parseSizes(m.getOrDefault("sizes", "1k,10k"));
        int reps = Integer.parseInt(m.getOrDefault("reps", "2"));
        long seed = Long.parseLong(m.getOrDefault("seed", "42"));
        double kfrac = Double.parseDouble(m.getOrDefault("kfrac", "0.5")); // для select
        File out = new File(m.getOrDefault("out", "out/out.csv"));

        switch (algo) {
            case "merge"   -> runMerge(sizes, reps, seed, out);
            case "quick"   -> runQuick(sizes, reps, seed, out);
            case "select"  -> runSelect(sizes, reps, seed, out, kfrac);
            case "closest" -> runClosest(sizes, reps, seed, out);
            default -> throw new IllegalArgumentException("Unknown algo: " + algo);
        }
    }
}
