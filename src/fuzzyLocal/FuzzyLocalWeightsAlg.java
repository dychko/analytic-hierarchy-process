package fuzzyLocal;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public interface FuzzyLocalWeightsAlg {
    public String getName();

    /**
     * Computes fuzzy vector of weights {w_L, w_M, w_U} by the given matrices {a_L, a_M, a_U}
     */
    public ArrayList<SimpleMatrix> computeFuzzyLocal(ArrayList<SimpleMatrix> fuzzyPairComparisons);
}
