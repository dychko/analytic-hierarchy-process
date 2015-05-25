package globalMethods;

import org.ejml.simple.SimpleMatrix;

/**
 * Class which implements Multiplicative synthesis method for computing global weights
 */

public class MultiplicativeSynthesis implements GlobalWeightsAlg {
    @Override
    public String getName() {
        return "Multiplicative synthesis";
    }

    @Override
    public SimpleMatrix computeGlobal(SimpleMatrix weightsCriteria, SimpleMatrix weightsElements) {
        SimpleMatrix weights = new SimpleMatrix(weightsElements.numRows(), 1);
        weights.set(1);
        for (int j = 0; j < weightsElements.numCols(); j++) {
            weights = weights.elementMult(weightsElements.extractVector(false, j).elementPower(weightsCriteria.get(j)));
        }
        return weights.divide(weights.elementSum());
    }
}
