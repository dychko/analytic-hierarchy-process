package fuzzyGlobal;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public interface FuzzyGlobalWeightsAlg {
    public String getName();
    /**
     * Computes fuzzy vector of global weights {w_L, w_U} by the given weights of criteria and elements
     * ArrayList {WC_L, WC_U}, ArrayList {WE_L, WE_U} - accordingly
     */
    public ArrayList<SimpleMatrix> computeFuzzyGlobal(ArrayList<SimpleMatrix> weightsCriteria,
                                                      ArrayList<SimpleMatrix> weightsElements);
}
