package com.example.codetest;

import java.util.Objects;

/**
 * Represents the total number of bags of a given size.
 * <p>
 * sizeKg  - the bag size in kg (e.g. 4)<br>
 * count   - how many bags of that size (may be fractional, e.g. 0.5 for a half bag)
 */
public class BagCount {

    private final int size;
    private final double count;

    public BagCount(int size, double count) {
        this.size = size;
        this.count = count;
    }

    public int getSize() {
        return size;
    }

    public double getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BagCount)) return false;
        BagCount bagCount = (BagCount) o;
        return size == bagCount.size
                && Double.compare(bagCount.count, count) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, count);
    }

    @Override
    public String toString() {
        return "BagCount{" +
                "size=" + size +
                ", count=" + count +
                '}';
    }
}
