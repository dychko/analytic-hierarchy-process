package globalMethods;

import org.ejml.simple.SimpleMatrix;

/**
 * Class which implements Distributed synthesis method for computing global weights
 */
public class DistributedSynthesis implements GlobalWeightsAlg {
    @Override
    public String getName() {
        return "Distributed synthesis";
    }

    @Override
    public SimpleMatrix computeGlobal(SimpleMatrix weightsCriteria, SimpleMatrix weightsElements) {
        SimpleMatrix weights = weightsElements.mult(weightsCriteria);
        return weights.divide(weights.elementSum());
    }

}
