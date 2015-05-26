package fuzzyRanging;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.max;

/**
 * Class which implements interval weights ranging using degree of preference
 */
public class DegreeOfPreference implements FuzzyRangingAlg {
    private final int W_L = 0;
    private final int W_U = 1;

    @Override
    public String getName() {
        return "Degree of preference method";
    }

    @Override
    public ArrayList<Integer> fuzzyRanging(ArrayList<SimpleMatrix> fuzzyVector) {

        SimpleMatrix prefMatrix = computePreferencesMatrix(fuzzyVector);
        SimpleMatrix prefSumVector = computePreferencesSumVector(prefMatrix);
        return findRangingByWeights(prefSumVector);
    }

    private SimpleMatrix computePreferencesMatrix(ArrayList<SimpleMatrix> fuzzyVector) {
        final int numElements = fuzzyVector.get(W_L).getNumElements();
        SimpleMatrix preferenceMatrix = new SimpleMatrix(numElements, numElements);
        for (int i = 0; i < numElements; i++) {
            for (int j = 0; j < numElements; j++) {
                if (i == j) {
                    preferenceMatrix.set(i, j, 0.5);
                } else {
                    ArrayList<Double> weight1 = getFuzzyNumber(fuzzyVector, i);
                    ArrayList<Double> weight2 = getFuzzyNumber(fuzzyVector, j);

                    preferenceMatrix.set(i, j, degreeOfPreference(weight1, weight2));
                }
            }
        }
        return preferenceMatrix;
    }

    private SimpleMatrix computePreferencesSumVector(SimpleMatrix prefMatrix) {
        SimpleMatrix preferences = new SimpleMatrix(prefMatrix.numRows(), 1);
        preferences.set(0);
        for (int j = 0; j < prefMatrix.numCols(); j++) {
            preferences = preferences.plus(prefMatrix.extractVector(false, j));
        }
        return  preferences;
    }

    private ArrayList<Integer> findRangingByWeights(SimpleMatrix prefSumVector) {
        ArrayIndexComparator comparator = new ArrayIndexComparator(prefSumVector);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);

        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < prefSumVector.numRows(); i++) {
            result.add(i, indexes[i]);
        }
        return result;
    }

    /**
     * Calculates degree of preference of fuzzyNumber1 over the fuzzyNumber2
     * @param fuzzyNumber1 ArrayList {v1_L, v1_U}, where v1_L, v1_U - numbers for low and up bounds
     * @param fuzzyNumber2 ArrayList {v2_L, v2_U}, where v2_L, v2_U - numbers for low and up bounds
     * @return degree of preference value
     */
    public double degreeOfPreference(ArrayList<Double> fuzzyNumber1, ArrayList<Double> fuzzyNumber2) {
        double fuzzyNumber1L = fuzzyNumber1.get(W_L);
        double fuzzyNumber1U = fuzzyNumber1.get(W_U);
        double fuzzyNumber2L = fuzzyNumber2.get(W_L);
        double fuzzyNumber2U = fuzzyNumber2.get(W_U);

        double value = (fuzzyNumber2U - fuzzyNumber1L) /
                ((fuzzyNumber1U - fuzzyNumber1L) + (fuzzyNumber2U - fuzzyNumber2L));

        return max(1 - max(value, 0), 0);
    }

    public ArrayList<Double> getFuzzyNumber(ArrayList<SimpleMatrix> fuzzyVector, int i) {
        ArrayList<Double> result = new ArrayList<>(2);
        result.add(W_L, fuzzyVector.get(W_L).get(i));
        result.add(W_U, fuzzyVector.get(W_U).get(i));

        return result;
    }
}
