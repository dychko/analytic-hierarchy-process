import fuzzyGlobal.FuzzyDistributedSynthesis;
import fuzzyGlobal.FuzzyGlobalWeightsAlg;
import fuzzyLocal.FRGMM;
import fuzzyLocal.FuzzyLocalWeightsAlg;
import fuzzyLocal.GPM;
import fuzzyRanging.DegreeOfPreference;
import fuzzyRanging.FuzzyRangingAlg;
import globalMethods.DistributedSynthesis;
import globalMethods.GlobalWeightsAlg;
import globalMethods.MultiplicativeSynthesis;
import localMethods.AN;
import localMethods.EM;
import localMethods.LocalWeightsAlg;
import localMethods.RGMM;
import org.ejml.simple.SimpleMatrix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        int hLayers[] = {3, 7, 7};
        Hierarchy mHierarchy = new Hierarchy(hLayers);

        System.out.println(mHierarchy);

        String pathReal = "C:\\users\\admin\\desktop\\generated\\real\\full\\";

        loadRealMatrices(pathReal, mHierarchy);

        PrintWriter writer;
        try {
            writer = new PrintWriter("Results.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<LocalWeightsAlg> localWeightsAlgs = new ArrayList<>();
        localWeightsAlgs.add(new EM());
        localWeightsAlgs.add(new RGMM());
        localWeightsAlgs.add(new AN());

        ArrayList<GlobalWeightsAlg> globalWeightsAlgs = new ArrayList<>();
        globalWeightsAlgs.add(new DistributedSynthesis());
        globalWeightsAlgs.add(new MultiplicativeSynthesis());

        for (LocalWeightsAlg localWeightsAlg : localWeightsAlgs) {
            mHierarchy.computeAllLocalWeights(localWeightsAlg);

            writer.println(localWeightsAlg.getName());
            for (int i = 0; i < mHierarchy.getNumLayers() - 1; i++) {
                for (int j = 0; j < mHierarchy.getNumElementsPerLayer(i); j++) {
                    writer.println("w, (" + (i + 1) + "," + j + ")");
                    writer.println(mHierarchy.getLocalWeights(i + 1, j));
                    writer.println("CI, (" + (i + 1) + "," + j + ")= " + mHierarchy.getConsistencyIndex(i + 1, j));
                }
            }
            writer.println("Global weights");
            for (GlobalWeightsAlg globalWeightsAlg : globalWeightsAlgs) {
                mHierarchy.computeAllGlobalWeights(globalWeightsAlg);

                writer.println(globalWeightsAlg.getName());
                writer.println(mHierarchy.getLayerGlobalWeights(3));

            }
        }


        // Fuzzy computations

        String pathFuzzy = "C:\\users\\admin\\desktop\\generated\\fuzzy\\full\\";

        loadFuzzyMatrices(pathFuzzy, mHierarchy);

        ArrayList<FuzzyLocalWeightsAlg> fuzzyLocalWeightsAlgs = new ArrayList<>();
        fuzzyLocalWeightsAlgs.add(new GPM());
        fuzzyLocalWeightsAlgs.add(new FRGMM());

        ArrayList<FuzzyGlobalWeightsAlg> fuzzyGlobalWeightsAlgs = new ArrayList<>();
        fuzzyGlobalWeightsAlgs.add(new FuzzyDistributedSynthesis());

        ArrayList<FuzzyRangingAlg> fuzzyRangingAlgs = new ArrayList<>();
        fuzzyRangingAlgs.add(new DegreeOfPreference());

        for (FuzzyLocalWeightsAlg fuzzyLocalWeightsAlg : fuzzyLocalWeightsAlgs) {
            mHierarchy.computeAllFuzzyLocalWeights(fuzzyLocalWeightsAlg);

            writer.println(fuzzyLocalWeightsAlg.getName());
            for (int i = 0; i < mHierarchy.getNumLayers() - 1; i++) {
                for (int j = 0; j < mHierarchy.getNumElementsPerLayer(i); j++) {
                    ArrayList<SimpleMatrix> weights = mHierarchy.getFuzzyLocalWeights(i + 1, j);
                    writer.println("wL, (" + (i + 1) + "," + j + ")");
                    writer.println(weights.get(0));
                    writer.println("wM, (" + (i + 1) + "," + j + ")");
                    writer.println(weights.get(1));
                    writer.println("wU, (" + (i + 1) + "," + j + ")");
                    writer.println(weights.get(2));
                }
            }
            writer.println("Global weights");
            for (FuzzyGlobalWeightsAlg fuzzyGlobalWeightsAlg : fuzzyGlobalWeightsAlgs) {
                mHierarchy.computeAllFuzzyGlobalWeights(fuzzyGlobalWeightsAlg);

                writer.println(fuzzyGlobalWeightsAlg.getName());
                ArrayList<SimpleMatrix> fuzzyGlobalWeights = mHierarchy.getLayerFuzzyGlobalWeights(3);

                writer.println("wL");
                writer.println(fuzzyGlobalWeights.get(0));
                writer.println("wU");
                writer.println(fuzzyGlobalWeights.get(1));


                writer.println("Ranging");
                for (FuzzyRangingAlg fuzzyRangingAlg : fuzzyRangingAlgs) {
                    ArrayList<Integer> ranging = fuzzyRangingAlg.fuzzyRanging(fuzzyGlobalWeights);

                    writer.println(fuzzyRangingAlg.getName());
                    writer.println(ranging);
                }
            }
        }

        writer.close();

    }

    public static void loadRealMatrices(String path, Hierarchy hierarchy) {
        try {
            for (int i = 1; i < hierarchy.getNumLayers(); i++) {
                for (int j = 0; j < hierarchy.getNumElementsPerLayer(i - 1); j++) {
                    String fileName = path + "full_pC" + Integer.toString(i - 1) + Integer.toString(j) +"g.csv";
                    hierarchy.setPairComparisonsMatrix(i, j, new SimpleMatrix().loadCSV(fileName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFuzzyMatrices(String path, Hierarchy hierarchy) {
        try {
            for (int i = 1; i < hierarchy.getNumLayers(); i++) {
                for (int j = 0; j < hierarchy.getNumElementsPerLayer(i - 1); j++) {
                    for (int k = 0; k < 3; k++) {
                        String index = "";
                        switch (k) {
                            case 0: index = "L"; break;
                            case 1: index = "M"; break;
                            case 2: index = "U"; break;
                        }
                        String fileName = path + "full_" + index + "full_pC" + Integer.toString(i - 1) + Integer.toString(j) +"g.csv";
                        hierarchy.setFuzzyPairComparisonsMatrix(i, j, k, new SimpleMatrix().loadCSV(fileName));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
