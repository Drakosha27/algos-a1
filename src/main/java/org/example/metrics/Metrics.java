package org.example.metrics;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    public static class Ctr {
        public long comparisons, copies, merges, inserts;
        public int  maxDepth;

        public void reset(){ comparisons=copies=merges=inserts=0; maxDepth=0; }
        public String toString(){
            return "cmp="+comparisons+", copy="+copies+", merges="+merges+", inserts="+inserts+", depth="+maxDepth;
        }
    }

    // Глобальный трекер глубины рекурсии (потокобезопасный на случай JUnit)
    private static final ThreadLocal<AtomicInteger> DEPTH = ThreadLocal.withInitial(AtomicInteger::new);

    public static void enter(Ctr c){
        int d = DEPTH.get().incrementAndGet();
        if (c!=null && d>c.maxDepth) c.maxDepth = d;
    }
    public static void leave(){ DEPTH.get().decrementAndGet(); }

    // Простой CSV writer (append)
    public static class Csv implements Closeable {
        private final PrintWriter out;
        public Csv(File file, String header) throws IOException {
            boolean exists = file.exists();
            this.out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8), true);
            if (!exists && header!=null && !header.isEmpty()) out.println(header);
        }
        public void row(Object... cells){
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<cells.length;i++){
                if (i>0) sb.append(',');
                sb.append(cells[i]);
            }
            out.println(sb.toString());
        }
        public void close(){ out.close(); }
    }
}