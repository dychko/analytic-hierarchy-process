package fuzzyGlobal;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class which implements Fuzzy distributed synthesis method for computing global weights
 */
public class FuzzyDistributedSynthesis implements FuzzyGlobalWeightsAlg {
    private int[] colNum;
    private double[] row;

    private final int W_L = 0;
    private final int W_U = 1;

    /**
     * Computes fuzzy global weights for given elements and criteria fuzzy weights
     * @param weightsCriteria ArrayList {WC_L, WC_U}, where WC_L - vector of criteria weights (lower bound)
     * @param weightsElements ArrayList {WE_L, WE_U}, where WE_L - matrix, which rows correspond to alternatives,
     *                        columns - corresponding criteria (lower bound)
     * @return {wGlob_L, wGlob_U}, where wGlob_L - global vector of alternative (lower bound)
     */
    @Override
    public ArrayList<SimpleMatrix> computeFuzzyGlobal(ArrayList<SimpleMatrix> weightsCriteria,
                                                      ArrayList<SimpleMatrix> weightsElements) {

        final int numAlternatives = weightsElements.get(W_L).numRows();

        ArrayList<SimpleMatrix> weights = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            weights.add(i, new SimpleMatrix(numAlternatives, 1));
        }

        for (int i = 0; i < numAlternatives; i++) {
            SimpleMatrix weightsElementL = weightsElements.get(W_L).extractVector(true, i);
            SimpleMatrix weightsElementU = weightsElements.get(W_U).extractVector(true, i);

            weights.get(W_L).set(i, findWeightsL(weightsCriteria, weightsElementL));
            weights.get(W_U).set(i, findWeightsU(weightsCriteria, weightsElementU));
        }

        return weights;
    }

    private double findWeightsL(ArrayList<SimpleMatrix> weightsCriteria, SimpleMatrix weightsElementL) {
        /**
         * Number of criteria = number of variables in the model
         */
        final int numCriteria = weightsCriteria.get(W_L).numRows();

        colNum = new int[numCriteria];
        row = new double[numCriteria];
        LpSolve lp;

        try {
            lp = LpSolve.makeLp(0, numCriteria);
            setLpConstraints(lp, weightsCriteria);

            // Set objective function
            lp.setAddRowmode(false);

            clearArrays();
            for (int i = 0; i < numCriteria; i++) {
                setVariable(i, weightsElementL.get(i));
            }
            lp.setObjFnex(numCriteria, row, colNum);

            lp.setMinim();
            lp.writeLp("global-model-l.lp");
            lp.solve();

            lp.printLp();
            double weightGlobal = lp.getObjective();
            lp.deleteLp();

            return weightGlobal;

        } catch (LpSolveException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private double findWeightsU(ArrayList<SimpleMatrix> weightsCriteria, SimpleMatrix weightsElementU) {
        /**
         * Number of criteria = number of variables in the model
         */
        final int numCriteria = weightsCriteria.get(W_U).numRows();

        colNum = new int[numCriteria];
        row = new double[numCriteria];
        LpSolve lp;

        try {
            lp = LpSolve.makeLp(0, numCriteria);
            setLpConstraints(lp, weightsCriteria);

            // Set objective function
            lp.setAddRowmode(false);

            clearArrays();
            for (int i = 0; i < numCriteria; i++) {
                setVariable(i, weightsElementU.get(i));
            }
            lp.setObjFnex(numCriteria, row, colNum);

            lp.setMaxim();
            lp.writeLp("global-model-u.lp");
            lp.solve();

            double weightGlobal = lp.getObjective();
            lp.deleteLp();

            return weightGlobal;

        } catch (LpSolveException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setLpConstraints(LpSolve lp, ArrayList<SimpleMatrix> weightsCriteria) {
        final int numCriteria = weightsCriteria.get(W_L).numRows();
        try {
            // Set column names for variables
            for (int i = 1; i <= numCriteria; i++) {
                lp.setColName(i, "w_crit_" + i);
            }

            lp.setAddRowmode(true);

            // Set constraint w_crit_i <= w_crit_U
            for (int i = 0; i < numCriteria; i++) {
                clearArrays();
                setVariable(i, 1);
                lp.addConstraintex(numCriteria, row, colNum, LpSolve.LE, weightsCriteria.get(W_U).get(i));
            }

            // Set constraint w_crit_i >= w_crit_L
            for (int i = 0; i < numCriteria; i++) {
                clearArrays();
                setVariable(i, 1);
                lp.addConstraintex(numCriteria, row, colNum, LpSolve.GE, weightsCriteria.get(W_L).get(i));
            }

            // Set constraint sum_i=1^n(w_crit_i)=1
            clearArrays();
            for (int j = 0; j < numCriteria; j++) {
                setVariable(j, 1);
            }
            lp.addConstraintex(numCriteria, row, colNum, LpSolve.EQ, 1);

        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

    private void setVariable(int index, double value) {
        colNum[index] = index + 1;
        row[index] = value;
    }

    private void clearArrays() {
        Arrays.fill(colNum, 0);
        Arrays.fill(row, 0);
    }
}
