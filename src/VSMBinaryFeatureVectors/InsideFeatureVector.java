package VSMBinaryFeatureVectors;

import java.util.ArrayList;

import no.uib.cipr.matrix.sparse.SparseVector;
import VSMSerialization.VSMFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public interface InsideFeatureVector {

	/**
	 * The method returns the inside feature vector corresponding to the inside
	 * tree
	 * 
	 * @param insideTree
	 * @param vectorBean
	 * @return
	 */

	// double[] getInsideFeatureVectorPhi(Tree<String> insideTree,
	// ArrayList<Alphabet> filteredDcitionary);

	SparseVector getInsideFeatureVectorPhi(Tree<String> insideTree,
			ArrayList<Alphabet> filteredDcitionary, FeatureVectorBean vectorBean);

}
