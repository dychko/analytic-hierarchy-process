package fuzzyLocal;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * Class which implements Fuzzy Row Geometric Mean Method for computing fuzzy local weights
 */
public class FRGMM implements FuzzyLocalWeightsAlg {
    private final int W_L = 0;
    private final int W_M = 1;
    private final int W_U = 2;

    @Override
    public String getName() {
        return "Fuzzy Row Geometric Mean Method";
    }

    @Override
    public ArrayList<SimpleMatrix> computeFuzzyLocal(ArrayList<SimpleMatrix> fuzzyPairComparisons) {
        int numCols = fuzzyPairComparisons.get(W_L).numCols();

        SimpleMatrix ones = new SimpleMatrix(numCols, 1);
        ones.set(1);

        ArrayList<SimpleMatrix> fuzzyLocal = createFuzzyVector(ones, ones, ones);

        for (int i = 0; i < numCols; i++) {
            ArrayList<SimpleMatrix> fuzzyColumn = createFuzzyVector(
                    fuzzyPairComparisons.get(W_L).extractVector(false, i),
                    fuzzyPairComparisons.get(W_M).extractVector(false, i),
                    fuzzyPairComparisons.get(W_U).extractVector(false, i));

            fuzzyLocal = fuzzyElementMult(fuzzyLocal, fuzzyColumn);
        }

        fuzzyLocal = fuzzyElementPower(fuzzyLocal, 1. / numCols);
        fuzzyLocal = fuzzyDivideByNumber(fuzzyLocal, fuzzyElementSum(fuzzyLocal));

        return fuzzyLocal;
    }

    private ArrayList<SimpleMatrix> fuzzyElementMult(ArrayList<SimpleMatrix> fuzzyVector1,
                                                     ArrayList<SimpleMatrix> fuzzyVector2) {

        SimpleMatrix resultL = fuzzyVector1.get(W_L).elementMult(fuzzyVector2.get(W_L));
        SimpleMatrix resultM = fuzzyVector1.get(W_M).elementMult(fuzzyVector2.get(W_M));
        SimpleMatrix resultU = fuzzyVector1.get(W_U).elementMult(fuzzyVector2.get(W_U));

        return createFuzzyVector(resultL, resultM, resultU);
    }

    private ArrayList<SimpleMatrix> fuzzyElementPower(ArrayList<SimpleMatrix> fuzzyVector, double power) {
        SimpleMatrix resultL = fuzzyVector.get(W_L).elementPower(power);
        SimpleMatrix resultM = fuzzyVector.get(W_M).elementPower(power);
        SimpleMatrix resultU = fuzzyVector.get(W_U).elementPower(power);

        return createFuzzyVector(resultL, resultM, resultU);
    }

    private ArrayList<Double> fuzzyElementSum(ArrayList<SimpleMatrix> fuzzyVector) {
        Double resultL = fuzzyVector.get(W_L).elementSum();
        Double resultM = fuzzyVector.get(W_M).elementSum();
        Double resultU = fuzzyVector.get(W_U).elementSum();

        return createFuzzyNumber(resultL, resultM, resultU);
    }

    private ArrayList<SimpleMatrix> fuzzyDivideByNumber(ArrayList<SimpleMatrix> fuzzyVector,
                                                       ArrayList<Double> fuzzyNumber ) {
        SimpleMatrix resultL = fuzzyVector.get(W_L).divide(fuzzyNumber.get(W_U));
        SimpleMatrix resultM = fuzzyVector.get(W_M).divide(fuzzyNumber.get(W_M));
        SimpleMatrix resultU = fuzzyVector.get(W_U).divide(fuzzyNumber.get(W_L));

        return createFuzzyVector(resultL, resultM, resultU);
    }

    private ArrayList<SimpleMatrix> createFuzzyVector(SimpleMatrix vectorL, SimpleMatrix vectorM, SimpleMatrix vectorU) {
        ArrayList<SimpleMatrix> result = new ArrayList<>(3);
        result.add(vectorL);
        result.add(vectorM);
        result.add(vectorU);

        return result;
    }

    private ArrayList<Double> createFuzzyNumber(Double valueL, Double valueM, Double valueU) {
        ArrayList<Double> result = new ArrayList<>(3);
        result.add(valueL);
        result.add(valueM);
        result.add(valueU);

        return result;
    }
}