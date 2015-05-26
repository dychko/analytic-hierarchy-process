package fuzzyRanging;

import org.ejml.simple.SimpleMatrix;

import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer>
{
    private final SimpleMatrix vector;

    public ArrayIndexComparator(SimpleMatrix vector) {
        this.vector = vector;
    }

    public Integer[] createIndexArray() {
        Integer[] indexes = new Integer[vector.getNumElements()];
        for (int i = 0; i < vector.getNumElements(); i++) {
            indexes[i] = i;
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
        return Double.valueOf(vector.get(index2)).compareTo(vector.get(index1));
    }
}
