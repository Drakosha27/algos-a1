# Algorithms Assignment 1

## Implemented Algorithms
- MergeSort (divide & conquer, insertion cutoff, reusable buffer)
- QuickSort (randomized pivot, tail recursion elimination)
- Deterministic Select (Median of Medians, O(n))
- Closest Pair of Points (O(n log n))

## Metrics
We collect:
- comparisons, copies, merges, inserts
- recursion depth
- execution time (ms)

## Results
Plots are saved in plots/:
![Time vs n](plots/time_vs_n.png)
![Depth vs n](plots/depth_vs_n.png)

## Analysis
- MergeSort: Θ(n log n), Master theorem case 2  
- QuickSort: Θ(n log n) expected (random pivot)  
- Select: Θ(n) worst case (Median-of-Medians)  
- Closest Pair: Θ(n log n), recurrence T(n)=2T(n/2)+O(n)

## Summary
Theoretical and measured complexities align closely, with minor constant-factor effects due to memory and cache.
