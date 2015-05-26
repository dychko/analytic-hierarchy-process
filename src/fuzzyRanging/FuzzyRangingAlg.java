package fuzzyRanging;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public interface FuzzyRangingAlg {
    String getName();
    public ArrayList<Integer> fuzzyRanging(ArrayList<SimpleMatrix> fuzzyVector);
}
