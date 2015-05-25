package localMethods;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * Class which implements Arithmetic Normalisation method for computing local weights
 */
public class AN implements LocalWeightsAlg {
    @Override
    public String getName() {
        return "Arithmetic Normalisation";
    }

    @Override
    public ArrayList computeLocal(SimpleMatrix pairwiseComparisons) {
        // Compute weight by AN
        int n = pairwiseComparisons.numCols();
        SimpleMatrix local = new SimpleMatrix(n, 1);
        for (int j = 0; j < n; j++) {
            SimpleMatrix column = pairwiseComparisons.extractVector(false, j);
            local.set(j, 1./ column.elementSum());
        }

        // Compute HCI
        double harmonicMean = n / local.elementSum();
        double hci = (harmonicMean - n) * (n + 1) / (n * (n - 1));

        // Norm weights
        local = local.divide(local.elementSum());

        // Return weights and HCI
        ArrayList results = new ArrayList(2);
        results.add(local);
        results.add(hci);
        return results;
    }

}
