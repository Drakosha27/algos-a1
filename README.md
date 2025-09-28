# Algorithms Assignment 1

Divide & Conquer algorithms — implementation, analysis, and report

- View Report on GitHub Pages: https://drakosha27.github.io/algos-a1/
- View Code on GitHub: https://github.com/Drakosha27/algos-a1

---

## Implemented Algorithms
- MergeSort: divide & conquer, linear merge, reusable buffer, insertion cutoff.
- QuickSort: randomized pivot; recurse only on smaller partition; tail-recursion elimination -> O(log n) stack.
- Deterministic Select (Median-of-Medians, O(n)): groups of 5, median-of-medians as pivot, recurse into the needed/smaller side.
- Closest Pair of Points (2D, O(n log n)): sort by x, recursive split, "strip" check in y-order with 7-8 neighbor scan.

---

## Metrics
We collect per run:
- comparisons
- copies, merges, inserts
- max recursion depth
- execution time (ms)

CSV files are written into the "out/" folder.

---

## How to Run
All algorithms are launched via the CLI Runner.

Compile:
mvn -q -DskipTests compile

Run MergeSort:
mvn -q exec:java -Dexec.mainClass=org.example.cli.Runner -Dexec.args="algo=merge sizes=1k,10k reps=2 seed=42 out=out/merge.csv"

Run QuickSort:
mvn -q exec:java -Dexec.mainClass=org.example.cli.Runner -Dexec.args="algo=quick sizes=1k,10k reps=2 seed=42 out=out/quick.csv"

Run Select:
mvn -q exec:java -Dexec.mainClass=org.example.cli.Runner -Dexec.args="algo=select sizes=1k,10k reps=2 seed=42 out=out/select.csv"

Run Closest Pair:
mvn -q exec:java -Dexec.mainClass=org.example.cli.Runner -Dexec.args="algo=closest sizes=1k,5k reps=2 seed=42 out=out/closest.csv"

---

## Results & Plots
After runs, build plots with Python:
py tools\make_plots.py

Plots are saved in "plots/":
- plots/time_vs_n.png
- plots/depth_vs_n.png

---

## Analysis (short)
- MergeSort: T(n) = 2T(n/2) + Theta(n) -> Master Theorem case 2 -> Theta(n log n).
- QuickSort: Expected Theta(n log n); typical recursion depth ~ 2*floor(log2 n) + O(1) with randomized pivot.
- Select: Deterministic Theta(n) worst case via Median-of-Medians pivot.
- Closest Pair: T(n) = 2T(n/2) + O(n) -> Theta(n log n).

---

## Project Structure
- src/main/java/...  — implementations (org.example.*, org.example.geometry.*)
- src/test/java/...  — tests
- tools/make_plots.py — plotting script
- out/               — CSV outputs
- plots/             — generated images
- README.md          — this report

