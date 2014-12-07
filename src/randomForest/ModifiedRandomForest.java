package randomForest;

import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.Utils;

public class ModifiedRandomForest extends RandomForest{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifiedRandomForest() {
		super();
	}
	
	@Override
	  public void buildClassifier(Instances data) throws Exception {

	    // can classifier handle the data?
	    getCapabilities().testWithFail(data);

	    // remove instances with missing class
	    data = new Instances(data);
	    data.deleteWithMissingClass();

	    m_bagger = new Bagging();

	    // RandomTree implements WeightedInstancesHandler, so we can
	    // represent copies using weights to achieve speed-up.
	    m_bagger.setRepresentCopiesUsingWeights(true);

	    J48 rTree = new J48();

	    // set up the random tree options
	    m_KValue = m_numFeatures;
	    if (m_KValue < 1) {
	      m_KValue = (int) Utils.log2(data.numAttributes() - 1) + 1;
	    }
//	    rTree.setKValue(m_KValue);
//	    rTree.setMaxDepth(getMaxDepth());
	    rTree.setDoNotCheckCapabilities(true);

	    // set up the bagger and build the forest
	    m_bagger.setClassifier(rTree);
	    m_bagger.setSeed(m_randomSeed);
	    m_bagger.setNumIterations(m_numTrees);
	    m_bagger.setCalcOutOfBag(true);
	    m_bagger.setNumExecutionSlots(m_numExecutionSlots);
	    m_bagger.buildClassifier(data);
	  }

}
