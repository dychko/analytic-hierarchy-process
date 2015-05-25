package localMethods;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * * Class which implements Row Geometric Mean Method for computing local weights
 */
public class RGMM implements LocalWeightsAlg {
    @Override
    public String getName() {
        return "Row Geometric Mean Method";
    }

    @Override
    public ArrayList computeLocal(SimpleMatrix pairwiseComparisons) {
        // Compute weight by RGMM
        int n = pairwiseComparisons.numCols();
        SimpleMatrix local = new SimpleMatrix(n, 1);
        local.set(1);
        for (int i = 0; i < n; i++) {
            SimpleMatrix column = pairwiseComparisons.extractVector(false, i);
            local = local.elementMult(column);
        }
        local = local.elementPower(1. / n);
        local = local.divide(local.elementSum());

        // Compute GCI
        SimpleMatrix e = new SimpleMatrix(n, n);
        double sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                e.set(i, j, pairwiseComparisons.get(i, j) * local.get(j) / local.get(i));
                sum += Math.pow(Math.log(e.get(i, j)), 2);
            }
        }
        double gci = 2. / ((n - 1) * (n - 2)) * sum;

        // Return weights and GCI
        ArrayList results = new ArrayList(2);
        results.add(local);
        results.add(gci);
        return results;
    }
}
