package org.example.cli;

import org.example.geometry.ClosestPair;
import org.example.geometry.ClosestPair.Point;
import org.example.geometry.ClosestPair.Result;
import org.example.metrics.Metrics;

import java.io.File;
import java.util.*;

public class Runner {

    private static Map<String,String> parseArgs(String[] args){
        Map<String,String> m = new HashMap<>();
        for (String a: args){
            int i = a.indexOf('=');
            if (i > 0) m.put(a.substring(0,i).trim(), a.substring(i+1).trim());
        }
        return m;
    }

    private static int[] parseSizes(String s){
        // поддержка "1k,5k,10000"
        String[] parts = s.split(",");
        int[] res = new int[parts.length];
        for (int i=0;i<parts.length;i++){
            String p = parts[i].trim().toLowerCase(Locale.ROOT);
            int mul = 1;
            if (p.endsWith("k")) { mul = 1_000; p = p.substring(0,p.length()-1); }
            else if (p.endsWith("m")) { mul = 1_000_000; p = p.substring(0,p.length()-1); }
            res[i] = Integer.parseInt(p) * mul;
        }
        return res;
    }

    private static Point[] makePoints(int n, Random rnd){
        Point[] a = new Point[n];
        for (int i=0;i<n;i++){
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            a[i] = new Point(x, y);
        }
        return a;
    }

    private static void runClosest(int[] sizes, int reps, long seed, File outFile) throws Exception {
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        try (Metrics.Csv csv = new Metrics.Csv(outFile, "algo,n,rep,timeMs,dist")) {
            for (int n : sizes) {
                for (int r = 1; r <= reps; r++) {
                    Random rnd = new Random(seed + 1000L*n + r);
                    Point[] pts = makePoints(n, rnd);
                    long t0 = System.nanoTime();
                    Result res = ClosestPair.closest(pts);
                    long t1 = System.nanoTime();
                    long ms = (t1 - t0) / 1_000_000L;
                    csv.row("closest", n, r, ms, String.format(Locale.ROOT, "%.9f", res.dist()));
                }
            }
        }
        System.out.println("Done. CSV -> " + outFile.getPath());
    }

    public static void main(String[] args) throws Exception {
        Map<String,String> m = parseArgs(args);
        String algo = m.getOrDefault("algo","merge");
        int[] sizes = parseSizes(m.getOrDefault("sizes","1k,10k"));
        int reps = Integer.parseInt(m.getOrDefault("reps","2"));
        long seed = Long.parseLong(m.getOrDefault("seed","42"));
        File out = new File(m.getOrDefault("out","out/out.csv"));

        switch (algo) {
            case "closest":
                runClosest(sizes, reps, seed, out);
                break;
            default:
                throw new IllegalArgumentException("Unknown algo: " + algo);
        }
    }
}