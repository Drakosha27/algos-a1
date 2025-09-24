package org.example.cli;

import org.example.MergeSort;

import java.io.BufferedWriter;
import java.nio.file.*;
import java.util.*;

public class Runner {
    static Map<String,String> parse(String[] args) {
        Map<String,String> m = new LinkedHashMap<>();
        for (String a : args) {
            int i = a.indexOf('=');
            if (i > 0) m.put(a.substring(0, i), a.substring(i + 1));
        }
        return m;
    }

    static int parseSizeToken(String tok) {
        tok = tok.trim().toLowerCase();
        int mul = 1;
        if (tok.endsWith("k")) { mul = 1_000; tok = tok.substring(0, tok.length()-1); }
        else if (tok.endsWith("m")) { mul = 1_000_000; tok = tok.substring(0, tok.length()-1); }
        return Integer.parseInt(tok) * mul;
    }

    static int[] make(int n, long seed){
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(1_000_000) - 500_000;
        return a;
    }

    public static void main(String[] args) throws Exception {
        Map<String,String> p = parse(args);
        String algo    = p.getOrDefault("algo",  "merge");
        String sizes   = p.getOrDefault("sizes", "1k,10k,100k");
        int    reps    = Integer.parseInt(p.getOrDefault("reps", "3"));
        long   seed    = Long.parseLong(p.getOrDefault("seed", "42"));
        Path   outPath = Paths.get(p.getOrDefault("out",  "out/results.csv"));
        if (outPath.getParent() != null) Files.createDirectories(outPath.getParent());

        try (BufferedWriter w = Files.newBufferedWriter(outPath)) {
            w.write("algo,n,run,nanos,ok,stats");
            w.newLine();

            for (String tok : sizes.split(",")) {
                int n = parseSizeToken(tok);
                int[] base = make(n, seed);
                for (int run = 1; run <= reps; run++) {
                    int[] a = Arrays.copyOf(base, base.length);
                    long t0 = System.nanoTime();
                    MergeSort.Stats st = MergeSort.sort(a);
                    long t1 = System.nanoTime();
                    boolean ok = MergeSort.isSorted(a);

                    w.write(String.join(",",
                            algo,
                            Integer.toString(n),
                            Integer.toString(run),
                            Long.toString(t1 - t0),
                            Boolean.toString(ok),
                            "\"" + st + "\""
                    ));
                    w.newLine();

                    System.out.printf("algo=%s n=%d run=%d time=%.3f ms ok=%s %s%n",
                            algo, n, run, (t1 - t0)/1e6, ok, st);
                }
            }
        }
        System.out.println("CSV -> " + outPath.toAbsolutePath());
    }
}