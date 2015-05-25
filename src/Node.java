import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public class Node {
    private String name;
    private double globalWeight;
    private SimpleMatrix pairComparisons;
    /**
     * Local weights of elements dependent on i-th criterion (of the previous layer)
     */
    private SimpleMatrix localWeights;

    private double consistencyIndex;

    /**
     * 0 - w_L; 1 - w_M; 2 - w_U
     */
    private ArrayList<Double> fuzzyGlobalWeight;
    private ArrayList<SimpleMatrix> fuzzyPairComparisons;
    private ArrayList<SimpleMatrix> fuzzyLocalWeights;

    public Node() {

    }

    public Node(String name, double globalWeight) {
        this.name = name;
        this.globalWeight = globalWeight;
    }

    public double getGlobalWeight() {
        return globalWeight;
    }

    public void setGlobalWeight(double globalWeight) {
        this.globalWeight = globalWeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleMatrix getPairComparisons() {
        return pairComparisons;
    }

    public void setPairComparisons(SimpleMatrix pairComparisons) {
        this.pairComparisons = pairComparisons;
    }

    public SimpleMatrix getLocalWeights() {
        return localWeights;
    }

    public void setLocalWeights(SimpleMatrix localWeights) {
        this.localWeights = localWeights;
    }

    public double getConsistencyIndex() {
        return consistencyIndex;
    }

    public void setConsistencyIndex(double consistencyIndex) {
        this.consistencyIndex = consistencyIndex;
    }

    public ArrayList<SimpleMatrix> getFuzzyLocalWeights() {
        return fuzzyLocalWeights;
    }

    public void setFuzzyLocalWeights(ArrayList<SimpleMatrix> fuzzyLocalWeights) {
        this.fuzzyLocalWeights = fuzzyLocalWeights;
    }

    public void setFuzzyLocalWeights(SimpleMatrix wl, SimpleMatrix wm, SimpleMatrix wu) {
        this.fuzzyLocalWeights.add(0, wl);
        this.fuzzyLocalWeights.add(1, wm);
        this.fuzzyLocalWeights.add(2, wu);
    }

    public ArrayList<SimpleMatrix> getFuzzyPairComparisons() {
        return fuzzyPairComparisons;
    }

    public SimpleMatrix getFuzzyPairComparisons(int fuzzyIndex) {
        return fuzzyPairComparisons.get(fuzzyIndex);
    }

    /**
     * Add or replace if exist fuzzy pair comparisons matrix
     * @param fuzzyIndex 0 - L, 1 - M, 2 - U
     * @param pairComparisons matrix of pair comparisons
     */
    public void setFuzzyPairComparisons(int fuzzyIndex, SimpleMatrix pairComparisons) {
        if (this.fuzzyPairComparisons == null) {
            this.fuzzyPairComparisons = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                this.fuzzyPairComparisons.add(i, null);
            }
        }
        this.fuzzyPairComparisons.set(fuzzyIndex, pairComparisons);
    }

    public void setFuzzyPairComparisons(ArrayList<SimpleMatrix> fuzzyPairComparisons) {
        this.fuzzyPairComparisons = fuzzyPairComparisons;
    }

    public void setFuzzyPairComparisons(SimpleMatrix al, SimpleMatrix am, SimpleMatrix au) {
        this.fuzzyPairComparisons.add(0, al);
        this.fuzzyPairComparisons.add(1, am);
        this.fuzzyPairComparisons.add(2, au);
    }

    public ArrayList<Double> getFuzzyGlobalWeight() {
        return fuzzyGlobalWeight;
    }

    public void setFuzzyGlobalWeight(ArrayList<Double> fuzzyGlobalWeight) {
        this.fuzzyGlobalWeight = fuzzyGlobalWeight;
    }

    public void setFuzzyGlobalWeight(double globalWeightL, double globalWeightM, double globalWeightU) {
        this.fuzzyGlobalWeight.add(0, globalWeightL);
        this.fuzzyGlobalWeight.add(1, globalWeightM);
        this.fuzzyGlobalWeight.add(2, globalWeightU);
    }
}
