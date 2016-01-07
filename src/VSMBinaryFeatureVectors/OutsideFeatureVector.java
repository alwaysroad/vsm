package VSMBinaryFeatureVectors;

import java.util.ArrayList;
import java.util.Stack;

import no.uib.cipr.matrix.sparse.SparseVector;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public interface OutsideFeatureVector {

	/**
	 * The method returns the inside feature vector corresponding to the inside
	 * tree
	 * 
	 * @param insideTree
	 * @return
	 */
	// double[] getOutsideFeatureVectorPsi(Stack<Tree<String>> foottoroot,
	// ArrayList<Alphabet> filteredDcitionary);
	// SparseVector getOutsideFeatureVectorPsi(Stack<Tree<String>> foottoroot,
	// ArrayList<Alphabet> filteredDcitionary);

	SparseVector getOutsideFeatureVectorPsi(Stack<Tree<String>> foottoroot,
			ArrayList<Alphabet> filteredDcitionary, FeatureVectorBean vectorBean);

}
