import pandas as pd, matplotlib.pyplot as plt
from pathlib import Path

out_dir = Path("plots"); out_dir.mkdir(exist_ok=True)

def load_csv(path):
    try:
        # Более терпимый парсер: engine="python", пропускаем «кривые» строки
        df = pd.read_csv(path, engine="python", on_bad_lines="skip")
        df.columns = [c.strip() for c in df.columns]
        # Если нет колонки algo — определим по имени файла
        if "algo" not in df.columns:
            algo_guess = Path(path).stem  # merge / quick / select / closest
            df["algo"] = algo_guess
        return df
    except Exception as e:
        print(f"skip {path}: {e}")
        return None

files = ["out/merge.csv","out/quick.csv","out/select.csv","out/closest.csv"]
dfs = [d for d in (load_csv(f) for f in files) if d is not None]

if not dfs:
    print("Нет пригодных CSV.")
    raise SystemExit(0)

# Оставим то, что нужно для графиков времени
time_cols = [c for c in ["algo","n","rep","timeMs"] if any(c in d.columns for d in dfs)]
need = {"algo","n","rep","timeMs"}
usable = []
for d in dfs:
    if need.issubset(d.columns):
        usable.append(d[list(need)])
    else:
        missing = need - set(d.columns)
        print(f"skip dataframe: нет колонок {missing}")

if not usable:
    print("Нет датафреймов с полным набором колонок (algo,n,rep,timeMs).")
    raise SystemExit(0)

all_df = pd.concat(usable, ignore_index=True)

# Преобразуем типы на всякий случай
for col in ["n","rep","timeMs"]:
    if col in all_df.columns:
        all_df[col] = pd.to_numeric(all_df[col], errors="coerce")
all_df = all_df.dropna(subset=["n","timeMs"])

# ===== График Time vs n =====
g = all_df.groupby(["algo","n"], as_index=False)["timeMs"].mean()
for algo, sub in g.groupby("algo"):
    sub = sub.sort_values("n")
    plt.plot(sub["n"], sub["timeMs"], marker="o", label=algo)
plt.xlabel("n")
plt.ylabel("time, ms")
plt.title("Time vs n")
plt.legend()
plt.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig(out_dir/"time_vs_n.png", dpi=150)
plt.clf()
print("Saved plots/time_vs_n.png")

# ===== График Depth vs n (если есть) =====
# Соберем любой depth, если вдруг присутствует в каких-то CSV
depth_dfs = []
for d in dfs:
    if {"algo","n","rep","depth"}.issubset(d.columns):
        dd = d[["algo","n","rep","depth"]].copy()
        dd["n"] = pd.to_numeric(dd["n"], errors="coerce")
        dd["depth"] = pd.to_numeric(dd["depth"], errors="coerce")
        depth_dfs.append(dd.dropna(subset=["n","depth"]))
if depth_dfs:
    d_all = pd.concat(depth_dfs, ignore_index=True)
    g2 = d_all.groupby(["algo","n"], as_index=False)["depth"].max()
    for algo, sub in g2.groupby("algo"):
        sub = sub.sort_values("n")
        plt.plot(sub["n"], sub["depth"], marker="o", label=algo)
    plt.xlabel("n")
    plt.ylabel("depth")
    plt.title("Depth vs n")
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(out_dir/"depth_vs_n.png", dpi=150)
    plt.clf()
    print("Saved plots/depth_vs_n.png")
else:
    print("Колонки depth не найдено — график глубины не построен.")
