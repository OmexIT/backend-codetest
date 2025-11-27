package com.example.codetest;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetBagCountsTest {

    @Test
    void returnsCorrectBagSizeCountsForSingleOrder() {
        List<Double> clientOrders = List.of(9.0);
        List<Integer> availableBagSizes = List.of(1, 2, 4);

        List<BagCount> result = GetBagCounts.getBagCounts(clientOrders, availableBagSizes);

        List<BagCount> expected = List.of(
                new BagCount(4, 2.0),
                new BagCount(2, 0.0),
                new BagCount(1, 1.0)
        );

        assertEquals(expected, result);
    }

    @Test
    void aggregatesAcrossMultipleOrders() {
        List<Double> clientOrders = List.of(5.0, 12.0, 12.0);
        List<Integer> availableBagSizes = List.of(1, 2, 4);

        List<BagCount> result = GetBagCounts.getBagCounts(clientOrders, availableBagSizes);

        List<BagCount> expected = List.of(
                new BagCount(4, 7.0),
                new BagCount(2, 0.0),
                new BagCount(1, 1.0)
        );

        assertEquals(expected, result);
    }

    @Test
    void supportsHalfBagsWhenNeeded() {
        List<Double> clientOrders = List.of(4.5);
        List<Integer> availableBagSizes = List.of(1, 2, 4);

        List<BagCount> result = GetBagCounts.getBagCounts(clientOrders, availableBagSizes);

        List<BagCount> expected = List.of(
                new BagCount(4, 1.0),
                new BagCount(2, 0.0),
                new BagCount(1, 0.5)
        );

        assertEquals(expected, result);
    }

    @Test
    void avoidsSplittingWhenWholeBagSolutionExists() {
        List<Double> clientOrders = List.of(6.0);
        List<Integer> availableBagSizes = List.of(1, 3, 4);

        List<BagCount> result = GetBagCounts.getBagCounts(clientOrders, availableBagSizes);

        List<BagCount> expected = List.of(
                new BagCount(4, 0.0),
                new BagCount(3, 2.0),
                new BagCount(1, 0.0)
        );

        assertEquals(expected, result);
    }

    @Test
    void prefersLargerSizesOnTies() {
        List<Double> clientOrders = List.of(4.0);
        List<Integer> availableBagSizes = List.of(1, 2, 3);

        List<BagCount> result = GetBagCounts.getBagCounts(clientOrders, availableBagSizes);

        List<BagCount> expected = List.of(
                new BagCount(3, 1.0),
                new BagCount(2, 0.0),
                new BagCount(1, 1.0)
        );

        assertEquals(expected, result);
    }

    @Test
    void rejectsOrdersNotInHalfKilogramIncrements() {
        List<Double> clientOrders = List.of(1.25);
        List<Integer> availableBagSizes = List.of(1, 2, 4);

        assertThrows(IllegalArgumentException.class,
                () -> GetBagCounts.getBagCounts(clientOrders, availableBagSizes));
    }

    @Test
    void throwsWhenOrderCannotBeFulfilledWithGivenBagSizes() {
        List<Double> clientOrders = List.of(4.5);
        List<Integer> availableBagSizes = List.of(2, 4);

        assertThrows(IllegalArgumentException.class,
                () -> GetBagCounts.getBagCounts(clientOrders, availableBagSizes));
    }
}
