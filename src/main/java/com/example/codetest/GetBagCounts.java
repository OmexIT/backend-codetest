package com.example.codetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implements the seed bag counting assignment.
 *
 * Rules:
 * - Prefer solutions that avoid splitting bags (fewest half bags).
 * - Then minimize the number of bags.
 * - Always return counts for every available bag size, sorted by size descending.
 * - Orders must be expressible in 0.5kg increments, otherwise an exception is thrown.
 */
public class GetBagCounts {

    public static List<BagCount> getBagCounts(
            List<Double> clientOrders,
            List<Integer> availableBagSizes) {

        if (clientOrders == null || availableBagSizes == null) {
            throw new IllegalArgumentException("clientOrders and availableBagSizes are required");
        }

        List<Integer> sortedBagSizes = availableBagSizes.stream()
                .filter(Objects::nonNull)
                .peek(size -> {
                    if (size <= 0) {
                        throw new IllegalArgumentException("Bag sizes must be positive");
                    }
                })
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (sortedBagSizes.isEmpty()) {
            throw new IllegalArgumentException("At least one bag size is required");
        }

        int[] totalPiecesPerSize = new int[sortedBagSizes.size()];

        for (Double order : clientOrders) {
            if (order == null) {
                throw new IllegalArgumentException("Client order cannot be null");
            }

            int orderUnits = toHalfKgUnits(order);
            BagSolution solution = solveOrder(orderUnits, sortedBagSizes);

            for (int i = 0; i < totalPiecesPerSize.length; i++) {
                totalPiecesPerSize[i] += solution.pieceCounts[i];
            }
        }

        List<BagCount> result = new ArrayList<>();
        for (int i = 0; i < sortedBagSizes.size(); i++) {
            double bags = totalPiecesPerSize[i] / 2.0;
            result.add(new BagCount(sortedBagSizes.get(i), bags));
        }

        return result;
    }

    private static BagSolution solveOrder(int orderUnits, List<Integer> bagSizes) {
        int[] workingCounts = new int[bagSizes.size()];
        BagSolution[] best = new BagSolution[]{null};
        search(0, orderUnits, bagSizes, workingCounts, best);

        if (best[0] == null) {
            throw new IllegalArgumentException("Order of " + (orderUnits / 2.0) + "kg cannot be fulfilled");
        }

        return best[0];
    }

    private static void search(
            int index,
            int remainingUnits,
            List<Integer> bagSizes,
            int[] counts,
            BagSolution[] bestHolder) {

        if (index == bagSizes.size()) {
            if (remainingUnits == 0) {
                BagSolution candidate = BagSolution.fromCounts(counts);
                if (isBetter(candidate, bestHolder[0])) {
                    bestHolder[0] = candidate;
                }
            }
            return;
        }

        int sizeUnits = bagSizes.get(index);
        int maxPieces = remainingUnits / sizeUnits;

        for (int pieces = maxPieces; pieces >= 0; pieces--) {
            counts[index] = pieces;
            int nextRemaining = remainingUnits - (pieces * sizeUnits);
            search(index + 1, nextRemaining, bagSizes, counts, bestHolder);
        }
        counts[index] = 0;
    }

    private static boolean isBetter(BagSolution candidate, BagSolution current) {
        if (candidate == null) {
            return false;
        }
        if (current == null) {
            return true;
        }

        if (candidate.halfBags != current.halfBags) {
            return candidate.halfBags < current.halfBags;
        }
        if (candidate.totalPieces != current.totalPieces) {
            return candidate.totalPieces < current.totalPieces;
        }

        for (int i = 0; i < candidate.pieceCounts.length; i++) {
            if (candidate.pieceCounts[i] != current.pieceCounts[i]) {
                return candidate.pieceCounts[i] > current.pieceCounts[i];
            }
        }

        return false;
    }

    private static int toHalfKgUnits(double orderKg) {
        if (orderKg <= 0) {
            throw new IllegalArgumentException("Orders must be positive");
        }

        double scaled = orderKg * 2;
        long rounded = Math.round(scaled);
        if (Math.abs(scaled - rounded) > 1e-9) {
            throw new IllegalArgumentException("Order " + orderKg + "kg is not in 0.5kg increments");
        }

        return (int) rounded;
    }

    private static final class BagSolution {
        private final int[] pieceCounts; // counts of half-bag pieces per size
        private final int totalPieces;
        private final int halfBags; // how many sizes have an odd number of pieces (splits)

        private BagSolution(int[] pieceCounts, int totalPieces, int halfBags) {
            this.pieceCounts = pieceCounts;
            this.totalPieces = totalPieces;
            this.halfBags = halfBags;
        }

        private static BagSolution fromCounts(int[] counts) {
            int[] copy = Arrays.copyOf(counts, counts.length);
            int totalPieces = 0;
            int halfBags = 0;
            for (int count : counts) {
                totalPieces += count;
                if ((count & 1) == 1) {
                    halfBags += 1;
                }
            }
            return new BagSolution(copy, totalPieces, halfBags);
        }
    }
}
