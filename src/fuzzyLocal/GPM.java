package fuzzyLocal;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class which implements Goal Programming Model method for computing fuzzy local weights
 */
public class GPM implements FuzzyLocalWeightsAlg {
    private int[] colNum;
    private double[] row;

    @Override
    public String getName() {
        return "Goal Programming Model";
    }

    @Override
    public ArrayList<SimpleMatrix> computeFuzzyLocal(ArrayList<SimpleMatrix> fuzzyPairComparisons) {
        /**
         * Dimension of matrices and weights vector
         */
        final int n = fuzzyPairComparisons.get(0).numRows();

        /**
         * Number of variables in the model
         */
        final int numCols = 8 * n;
        colNum = new int[numCols];
        row = new double[numCols];
        LpSolve lp;

        // Indices for problem variables
        final int EP_IDX = 0;     // Index shift for variable e+
        final int EM_IDX =     n; // Index shift for variable e-
        final int GP_IDX = 2 * n; // Index shift for variable g+
        final int GM_IDX = 3 * n; // Index shift for variable g-
        final int DT_IDX = 4 * n; // Index shift for variable delta
        final int WL_IDX = 5 * n; // Index shift for variable wL
        final int WM_IDX = 6 * n; // Index shift for variable wM
        final int WU_IDX = 7 * n; // Index shift for variable wU

        try {
            lp = LpSolve.makeLp(0, numCols);

            // Set column names for variables
            for (int i = 1; i <= n; i++) {
                lp.setColName(i + EP_IDX, "e+_" + i);
                lp.setColName(i + EM_IDX, "e-_" + i);
                lp.setColName(i + GP_IDX, "g+_" + i);
                lp.setColName(i + GM_IDX, "g-_" + i);
                lp.setColName(i + DT_IDX, "dt_" + i);
                lp.setColName(i + WL_IDX, "wL_" + i);
                lp.setColName(i + WM_IDX, "wM_" + i);
                lp.setColName(i + WU_IDX, "wU_" + i);
            }

            lp.setAddRowmode(true);

            // Set constraint: E+ + E- = (AL-I)wU - (n-1)wL
            for (int i = 0; i < n; i++) {
                // Reset values for columns and row
                clearArrays();
                setVariable(i + EP_IDX, 1); // Column for E+
                setVariable(i + EM_IDX, 1); // Column for E-
                setVariable(i + WL_IDX, n - 1); // Column for wL

                for (int j = 0; j < n; j++) { // Columns for wU
                    double c = fuzzyPairComparisons.get(0).get(i, j); // get aL
                    if (i == j) {
                        c -= 1;
                    }
                    setVariable(WU_IDX + j, -c);
                }
                lp.addConstraintex(numCols, row, colNum, LpSolve.EQ, 0);
            }

            // Set constraint: G+ + G- = (AU-I)wL - (n-1)wU
            for (int i = 0; i < n; i++) {
                clearArrays();
                setVariable(i + GP_IDX, 1); // Column for G+
                setVariable(i + GM_IDX, 1); // Column for G-
                setVariable(i + WU_IDX, n - 1); // Column for wU

                for (int j = 0; j < n; j++) { // Columns for wL
                    double c = fuzzyPairComparisons.get(2).get(i, j); // get aU
                    if (i == j) {
                        c -= 1;
                    }
                    setVariable(WL_IDX + j, -c);
                }
                lp.addConstraintex(numCols, row, colNum, LpSolve.EQ, 0);
            }

            // Set constraint D = (AM - nI)wM
            for (int i = 0; i < n; i++) {
                clearArrays();
                setVariable(i + DT_IDX, 1); // Column for d

                for (int j = 0; j < n; j++) { // Columns for wM
                    double c = fuzzyPairComparisons.get(1).get(i, j); // get aM
                    if (i == j) {
                        c -= n;
                    }
                    setVariable(WM_IDX + j, -c);
                }
                lp.addConstraintex(numCols, row, colNum, LpSolve.EQ, 0);
            }

            // Set constraint sum_j=1^n,i!=j(wU_j) + wL_i>=1, i=1:n
            for (int i = 0; i < n; i++) {
                clearArrays();
                setVariable(i + WL_IDX, 1); // Column for wL_i

                for (int j = 0; j < n; j++) { // Columns for wU_j
                    if (i == j)
                        continue;
                    setVariable(WU_IDX + j, 1);
                }
                lp.addConstraintex(numCols, row, colNum, LpSolve.GE, 1);
            }

            // Set constraint sum_j=1^n,i!=j(wL_j) + wU_i<=1, i=1:n
            for (int i = 0; i < n; i++) {
                clearArrays();
                setVariable(i + WU_IDX, 1); // Column for wU_i

                for (int j = 0; j < n; j++) { // Columns for wL_j
                    if (i == j)
                        continue;
                    setVariable(WL_IDX + j, 1);
                }
                lp.addConstraintex(numCols, row, colNum, LpSolve.LE, 1);
            }

            // Set constraint sum_i=1^n(wM_i)=1
            clearArrays();
            for (int j = 0; j < n; j++) {
                setVariable(WM_IDX + j, 1);
            }
            lp.addConstraintex(numCols, row, colNum, LpSolve.EQ, 1);

            // Set constraint wU - wM >= 0
            for (int i = 0; i < n; i++) {
                clearArrays();
                setVariable(i + WU_IDX, 1); // Column for wU_i
                setVariable(i + WM_IDX, -1); // Column for wM_i
                lp.addConstraintex(numCols, row, colNum, LpSolve.GE, 0);
            }

            // Set constraint wM - wL >= 0
            for (int i = 0; i < n; i++) {
                clearArrays();
                setVariable(i + WM_IDX, 1); // Column for wM_i
                setVariable(i + WL_IDX, -1); // Column for wL_i
                lp.addConstraintex(numCols, row, colNum, LpSolve.GE, 0);
            }

            // Set constraints >= 0 for:
            // e+ >= 0
            // e- >= 0
            // g+ >= 0
            // g- >= 0
            // dt >= 0
            // wL >= 0
            for (int i = 0; i < WM_IDX; i++) {
                clearArrays();
                setVariable(i, 1);
                lp.addConstraintex(numCols, row, colNum, LpSolve.GE, 0);
            }

            // Set objective function
            lp.setAddRowmode(false);

            clearArrays();
            for (int i = 0; i < WL_IDX; i++) {
                setVariable(i, 1);
            }
            lp.setObjFnex(5 * n, row, colNum);

            lp.setMinim();

            lp.writeLp("model.lp");
            lp.setVerbose(LpSolve.IMPORTANT);

            lp.solve();

            lp.printLp();

            /* objective value */
            System.out.println("Objective value: " + lp.getObjective());
            /* variable values */
            lp.getVariables(row);
            for(int j = 0; j < numCols; j++)
                System.out.println(lp.getColName(j + 1) + ": " + row[j]);

            lp.deleteLp();

            SimpleMatrix wl = new SimpleMatrix(n, 1);
            SimpleMatrix wm = new SimpleMatrix(n, 1);
            SimpleMatrix wu = new SimpleMatrix(n, 1);

            for (int i = 0; i < n; i++) {
                wl.set(i, row[i + WL_IDX]);
                wm.set(i, row[i + WM_IDX]);
                wu.set(i, row[i + WU_IDX]);
            }

            ArrayList<SimpleMatrix> weights = new ArrayList<>(3);
            weights.add(0, wl);
            weights.add(1, wm);
            weights.add(2, wu);

            return weights;
        } catch (LpSolveException e) {
            e.printStackTrace();
            return null;
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
