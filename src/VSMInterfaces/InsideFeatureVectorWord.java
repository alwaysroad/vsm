package VSMInterfaces;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import no.uib.cipr.matrix.sparse.SparseVector;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public interface InsideFeatureVectorWord {

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

	SparseVector getInsideFeatureVectorPhi(Tree<String> insideTree,
			Alphabet wordDictionary, VSMWordFeatureVectorBean vectorBean);

}
