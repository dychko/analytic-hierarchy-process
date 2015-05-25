package localMethods;

import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * Class which implements Eigenvalue Method for computing local weights
 */
public class EM implements LocalWeightsAlg {
    @Override
    public String getName() {
        return "Eigenvalue Method";
    }

    @Override
    public ArrayList computeLocal(SimpleMatrix pairwiseComparisons) {
        // Find eigenvector corresponding to the maximum eigenvalue
        SimpleEVD evd = pairwiseComparisons.eig();
        SimpleMatrix ev = evd.getEigenVector(evd.getIndexMax());
        ev = ev.divide(ev.elementSum());

        // Find consistency index (CI)
        int n = pairwiseComparisons.numCols();
        double lambda = evd.getEigenvalue(evd.getIndexMax()).getReal();
        double consistency = (lambda - n) / (n - 1);

        // Return weights and CI
        ArrayList results = new ArrayList(2);
        results.add(ev);
        results.add(consistency);
        return results;
    }
}
