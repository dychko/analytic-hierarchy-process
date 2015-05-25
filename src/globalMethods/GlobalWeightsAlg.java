package globalMethods;

import org.ejml.simple.SimpleMatrix;

public interface GlobalWeightsAlg {
    String getName();
    SimpleMatrix computeGlobal(SimpleMatrix weightsCriteria, SimpleMatrix weightsElements);
}
