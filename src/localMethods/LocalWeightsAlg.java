package localMethods;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public interface LocalWeightsAlg {
    public String getName();
    public ArrayList computeLocal(SimpleMatrix pairwiseComparisons);
}
