import fuzzyLocal.FuzzyLocalWeightsAlg;
import globalMethods.GlobalWeightsAlg;
import localMethods.LocalWeightsAlg;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public class Hierarchy {
    private ArrayList<ArrayList<Node>> hStructure;

//    public Hierarchy() {
//
//    }

    /**
     * Build hierarchy with Goal on 1-st level and with number of elements,
     * corresponding to the 'elements' array
     * @param elements specifies number of elements on (i+1)th layer, starting with 2nd layer
     */
    public Hierarchy(int[] elements) {
        hStructure = new ArrayList<>(elements.length + 1);

        ArrayList<Node> goal = new ArrayList<>(1);
        goal.add(new Node());
        hStructure.add(0, goal);

        for (int i = 0; i < elements.length; i++) {
            if (elements[i] <= 0) throw new IllegalArgumentException("Number of elements should be greater than zero");
            ArrayList<Node> layer = new ArrayList<>(elements[i]);
            for (int j = 0; j < elements[i]; j++) {
                layer.add(j, new Node());
            }
            hStructure.add(i + 1, layer);
        }
    }

    public int getNumLayers() {
        return hStructure.size();
    }

    public int getNumElementsPerLayer(int layer) {
        return hStructure.get(layer).size();
    }

    /**
     * Sets the matrix of pair comparisons
     * @param layer index of layer which matrix was built for. Goal has layer with index 0.
     * @param element index of element according to which comparisons was conducted
     * @param pairComparisons matrix of pair comparisons that should satisfy definition
     */
    public void setPairComparisonsMatrix(int layer, int element, SimpleMatrix pairComparisons) {
        checkElementLayer(layer, element);
        if (pairComparisons.numCols() != pairComparisons.numRows() ||
            pairComparisons.numCols() != hStructure.get(layer).size()) {
            throw new IndexOutOfBoundsException("Matrix has incorrect dimensions");
        }
        // TODO: Better check for pairComparison matrix might be needed
        hStructure.get(layer - 1).get(element).setPairComparisons(pairComparisons);
    }

    /**
     * Returns pair comparisons matrix for given layer and element
     * @param layer layer of compared elements
     * @param element element relatively to which lower layer elements are compared
     * @return Pair comparisons matrix
     */
    public SimpleMatrix getPairComparisonsMatrix(int layer, int element) {
        checkElementLayer(layer, element);
        return hStructure.get(layer - 1).get(element).getPairComparisons();
    }

    /**
     * Sets fuzzy matrix of pair comparisons
     * @param layer index of layer which matrix was built for. Goal has layer with index 0.
     * @param element index of element according to which comparisons was conducted
     * @param fuzzyIndex 0 - L, 1 - M, 2 - U
     * @param pairComparisons matrix of pair comparisons (either L or M or U)
     */
    public void setFuzzyPairComparisonsMatrix(int layer, int element, int fuzzyIndex, SimpleMatrix pairComparisons) {
        checkElementLayer(layer, element);
        if (pairComparisons.numCols() != pairComparisons.numRows() ||
                pairComparisons.numCols() != hStructure.get(layer).size()) {
            throw new IndexOutOfBoundsException("Matrix has incorrect dimensions");
        }
        hStructure.get(layer - 1).get(element).setFuzzyPairComparisons(fuzzyIndex, pairComparisons);
    }

    /**
     * Returns fuzzy pair comparisons matrix for given layer and element
     * @param layer layer of compared elements
     * @param element element relatively to which lower layer elements are compared
     * @param fuzzyIndex 0 - L, 1 - M, 2 - U
     * @return Pair comparisons matrix
     */
    public SimpleMatrix getFuzzyPairComparisonsMatrix(int layer, int element, int fuzzyIndex) {
        checkElementLayer(layer, element);
        return hStructure.get(layer - 1).get(element).getFuzzyPairComparisons(fuzzyIndex);
    }

    public void computeAllLocalWeights(LocalWeightsAlg localWeightsAlg) {
        for (int i = 0; i < hStructure.size() - 1; i++) {
            for (Node node : hStructure.get(i)) {
                if (node.getPairComparisons() == null) {
                    throw new NullPointerException("Null matrix of pairwise comparisons in structure");
                }
                ArrayList results = localWeightsAlg.computeLocal(node.getPairComparisons());
                node.setLocalWeights((SimpleMatrix) results.get(0));
                node.setConsistencyIndex((double) results.get(1));
            }
        }
    }

    public void computeAllFuzzyLocalWeights(FuzzyLocalWeightsAlg fuzzyLocalWeightsAlg) {
        for (int i = 0; i < hStructure.size() - 1; i++) {
            for (Node node : hStructure.get(i)) {
                if (node.getFuzzyPairComparisons().get(0) == null ||
                    node.getFuzzyPairComparisons().get(1) == null ||
                    node.getFuzzyPairComparisons().get(2) == null) {
                    throw new NullPointerException("Null fuzzy matrix of pairwise comparisons in structure");
                }

                ArrayList<SimpleMatrix> results = fuzzyLocalWeightsAlg.computeFuzzyLocal(node.getFuzzyPairComparisons());
                node.setFuzzyLocalWeights(results);
            }
        }
    }

    public SimpleMatrix getLocalWeights(int layer, int element) {
        checkElementLayer(layer, element);
        return hStructure.get(layer - 1).get(element).getLocalWeights();
    }

    public double getConsistencyIndex(int layer, int element) {
        checkElementLayer(layer, element);
        return hStructure.get(layer - 1).get(element).getConsistencyIndex();
    }

    public ArrayList<SimpleMatrix> getFuzzyLocalWeights(int layer, int element) {
        checkElementLayer(layer, element);
        return hStructure.get(layer - 1).get(element).getFuzzyLocalWeights();
    }

    public void computeAllGlobalWeights(GlobalWeightsAlg globalWeightsAlg) {
        // Check if all local weights vectors are present
        for (int i = 0; i < hStructure.size() - 1; i++) {
            for (Node node : hStructure.get(i)) {
                if (node.getLocalWeights() == null) {
                    throw new NullPointerException("Null vector of local weights in structure");
                }
            }
        }

        // Set global weight for goal = 1
        hStructure.get(0).get(0).setGlobalWeight(1);

        if (hStructure.size() == 1) {
            return;
        }

        // Set global weights for layer 1 (if exist) the same as local weights
        ArrayList<Node> layer1 = hStructure.get(1);
        for (int i = 0; i < layer1.size(); i++) {
            double globWeight = hStructure.get(0).get(0).getLocalWeights().get(i);
            layer1.get(i).setGlobalWeight(globWeight);
        }

        // Set global weights for other layers by given algorithm
        for (int i = 1; i < hStructure.size() - 1; i++) {

            // Combine all local weights into 1 matrix 'localLayer'
            SimpleMatrix localLayer = hStructure.get(i).get(0).getLocalWeights();
            for (int j = 1; j < hStructure.get(i).size(); j++) {
                localLayer = localLayer.combine(0, localLayer.numCols(), hStructure.get(i).get(j).getLocalWeights());
            }

            // Combine all global weights into 1 vector 'criteria'
            SimpleMatrix criteria = new SimpleMatrix(hStructure.get(i).size(), 1);
            for (int j = 0; j < hStructure.get(i).size(); j++) {
                criteria.set(j, hStructure.get(i).get(j).getGlobalWeight());
            }

            // Find global weights for level
            SimpleMatrix global = globalWeightsAlg.computeGlobal(criteria, localLayer);

            // Set global weights for nodes
            for (int j = 0; j < hStructure.get(i + 1).size(); j++) {
                hStructure.get(i + 1).get(j).setGlobalWeight(global.get(j));
            }
        }
    }

    public double getGlobalWeight(int layer, int element) {
        if (layer < 0 || layer >= hStructure.size()) {
            throw new IndexOutOfBoundsException("Incorrect layer index");
        }
        if (element < 0 || element >= hStructure.get(layer).size()) {
            throw new IndexOutOfBoundsException("Incorrect element index");
        }
        return hStructure.get(layer).get(element).getGlobalWeight();
    }

    public SimpleMatrix getLayerGlobalWeights(int layer) {
        if (layer < 0 || layer >= hStructure.size()) {
            throw new IndexOutOfBoundsException("Incorrect layer index");
        }
        ArrayList<Node> hLayer = hStructure.get(layer);
        SimpleMatrix global = new SimpleMatrix(hLayer.size(), 1);
        for (int i = 0; i < hLayer.size(); i++) {
            global.set(i, hLayer.get(i).getGlobalWeight());
        }
        return global;
    }
//
//    public int getNumLayers() {
//        return hStructure.size();
//    }
//
//    public int getNumElementsPerLayer(int layer) {
//        return hStructure.get(layer).size();
//    }
//
//    public void setLayers(int numLayers) {
//        hStructure = new ArrayList<>(numLayers);
//    }
//
//    public void setElementsPerLayer(int layer, int numElements) {
//        hStructure.set(layer, new ArrayList<>(numElements));
//    }

    public void checkElementLayer(int layer, int element) {
        if (layer <= 0 || layer >= hStructure.size()) {
            throw new IndexOutOfBoundsException("Incorrect layer index");
        }
        if (element < 0 || element >= hStructure.get(layer - 1).size()) {
            throw new IndexOutOfBoundsException("Incorrect element index");
        }
    }

    @Override
    public String toString() {
        String hierarchy = "";
        for (int i = 0; i < hStructure.size(); i++) {
            for (int j = 0; j < hStructure.get(i).size(); j++) {
                hierarchy += "{" + (i) + "_" + (j) + "} ";
            }
            hierarchy += "\n";
        }
        return hierarchy;
    }
}
