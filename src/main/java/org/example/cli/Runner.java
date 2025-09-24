package org.example.cli;

import org.example.MergeSort;
import org.example.QuickSort;
import org.example.Select;
import org.example.geometry.ClosestPair;
import org.example.metrics.Metrics;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class Runner {
    public static void main(String[] args) throws Exception {
        // Простейший парсер аргументов вида key=value
        var map = new java.util.HashMap<String,String>();
        for (String arg : args) {
            String[] kv = arg.split("=",2);
            if (kv.length==2) map.put(kv[0], kv[1]);
        }

        String algo = map.getOrDefault("algo", "merge");
        String sizesArg = map.getOrDefault("sizes", "100,1000");
        int reps = Integer.parseInt(map.getOrDefault("reps","3"));
        long seed = Long.parseLong(map.getOrDefault("seed","42"));
        String outFile = map.getOrDefault("out", "result.csv");

        int[] sizes = Arrays.stream(sizesArg.split(","))
                .map(s -> s.replace("k","000"))
                .mapToInt(Integer::parseInt)
                .toArray();

        try (Metrics.Csv csv = new Metrics.Csv(new File(outFile),
                "algo,n,cmp,copy,merges,inserts,depth,ok")) {
            for (int n : sizes) {
                Random r = new Random(seed);
                int[] src = r.ints(n, -1_000_000, 1_000_000).toArray();

                for (int rep=0; rep<reps; rep++) {
                    int[] a = Arrays.copyOf(src, src.length);
                    Metrics.Ctr ctr = new Metrics.Ctr();

                    boolean ok = switch (algo) {
                        case "merge" -> {
                            Metrics.enter(ctr);
                            MergeSort.sort(a);
                            Metrics.leave();
                            yield MergeSort.isSorted(a);
                        }
                        case "quick" -> {
                            QuickSort.sort(a);
                            yield QuickSort.isSorted(a);
                        }
                        case "select" -> {
                            int k = a.length/2;
                            int v = Select.select(a, k);
                            Arrays.sort(a);
                            yield v == a[k];
                        }
                        default -> throw new IllegalArgumentException("Unknown algo: " + algo);
                    };

                    csv.row(algo, n, ctr.comparisons, ctr.copies, ctr.merges, ctr.inserts, ctr.maxDepth, ok);
                }
            }
        }
    }
}
