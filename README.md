# Assignment 1 — Divide & Conquer

## Architecture notes
- **MergeSort**: линейное слияние, один переиспользуемый буфер, cut-off на InsertionSort.
- **QuickSort**: рандомный pivot; рекурсия ТОЛЬКО на меньшей части; большая — итеративно ⇒ стек O(log n) в среднем.
- **Deterministic Select (MoM5)**: группы по 5, «медиана медиан» как pivot; рекурсия только в нужной половине ⇒ O(n).
- **Closest Pair (2D)**: сортировка по x; рекурсивный сплит; поддержка порядка по y; в «полосе» до 7 соседей.

## Recurrence (кратко)
- MergeSort: T(n)=2T(n/2)+Θ(n) → Master, Case 2 → **Θ(n log n)**.
- QuickSort (ожид.): T(n)=T(αn)+T((1−α)n)+Θ(n) → в ср. **Θ(n log n)** (худш. Θ(n²) редок из-за рандомизации).
- Select (MoM5): T(n)=T(n/5)+T(7n/10)+Θ(n) → Akra–Bazzi → **Θ(n)**.
- Closest Pair: T(n)=2T(n/2)+Θ(n) → **Θ(n log n)**, ≤7 проверок в «полосе».

## Measurements
CSV генерируются `Runner`:
- `out/merge.csv`, `out/quick.csv`, `out/select.csv`, `out/closest.csv`.
- Формат строк: `algo,n,rep,timeMs,...` (для closest ещё `dist`).

## Plots (будут добавлены)
- Time vs n (merge/quick/select/closest)
- Depth vs n (quick)
- Краткая дискуссия о константах (cache/GC) и совпадении с теорией.

## Git workflow (done/plan)
- Ветки: `feature/metrics`, `feature/mergesort`, `feature/quicksort`, `feature/select`, `feature/closest`, `feature/cli`.
- Теги: `v0.1` (уже), затем `v1.0` после финализации отчёта и графиков.
