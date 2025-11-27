# Seed Bag Counts – Reference Solution Guide

This outlines the intended solution for `GetBagCounts.getBagCounts`. The repository keeps the method stubbed for candidates; use this guide for review/verification.

## Core rules
- Orders are in 0.5 kg increments only; reject anything else.
- Return all bag sizes, sorted descending, even when the count is zero.
- Prefer fewer splits (half bags), then fewer total bags, then larger sizes on ties.
- Throw when an order cannot be fulfilled with the provided bag sizes.

## Approach
1) **Normalize units:** Convert kg to half‑kg units (e.g., 4.5 kg → 9). Do the same for bag sizes (e.g., size 4 kg → 8 units). This avoids floating point issues.
2) **Validate input:** Non‑null lists, positive bag sizes, positive orders, and half‑kg increments.
3) **Sort bag sizes:** Descending, keep distinct.
4) **Per‑order search:** For each order (in units), find the best combination of pieces (each piece = half bag of a size). A simple exhaustive/backtracking search over sizes works because bag sizes are few.
   - Track counts per size (in pieces).
   - When a combination exactly matches the order, score it by:
     - `splitCount`: how many sizes have an odd number of pieces (i.e., a half bag is needed).
     - `bagCount`: total pieces / 2 (but you can compare in piece counts).
     - Tiebreaker: prefer using more of larger sizes (bags sorted descending).
5) **Aggregate totals:** Sum piece counts across all clients per size; convert pieces to bags (`pieces / 2.0`) for the final `BagCount` list.
6) **Return list:** `List<BagCount>` in descending size order.

## Reference pseudocode
```
validate inputs
sizes = sortDistinctDesc(availableBagSizes)
totals = int[sizes.length] // piece counts

for orderKg in clientOrders:
  units = toUnits(orderKg) // kg * 2, must be int
  best = search(0, units, sizes, new int[sizes.length])
  if best == null: throw
  totals += best.pieceCounts

result = []
for i, size in sizes:
  result.add(new BagCount(size, totals[i] / 2.0))
return result
```

`search(idx, remaining, sizes, counts)` explores counts for each size (from max possible down to 0), recursing to the next size until `remaining == 0`. Use the scoring rules above to choose the best combination.

## Complexity
- With few bag sizes (e.g., 3–5), exhaustive search is fine: `O((order/maxSize)^(bagSizes))` in the worst case but small in practice. A DP/coin‑change variant with a composite cost (splits, pieces, then lexicographic preference) also works in `O(order * bagSizes)`.

## Edge cases to cover
- Orders with `.5` (half bag) and whole numbers.
- Non‑0.5 increments → throw.
- No feasible combination given bag sizes → throw.
- Duplicate bag sizes; zero/negative sizes → throw.
- Custom bag sets (e.g., 1, 3, 4 for a 6 kg order should choose 2 × 3kg, no splits).

## Suggested tests
- Single order, standard sizes (1/2/4): 9 kg → `{4:2, 2:0, 1:1}`.
- Multiple orders aggregation: [5, 12, 12] with sizes 1/2/4.
- Half‑bag support: 4.5 kg with sizes 1/2/4 → `{4:1, 2:0, 1:0.5}`.
- Avoid splits: 6 kg with sizes 1/3/4 → `{3:2, 1:0, 4:0}`.
- Reject 1.25 kg with sizes 1/2/4.
- Reject 4.5 kg with sizes 2/4 (no way to make 0.5).
