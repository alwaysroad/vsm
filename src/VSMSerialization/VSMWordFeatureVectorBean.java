package VSMSerialization;

import java.util.ArrayList;
import java.util.Stack;

import VSMUtilityClasses.Alphabet;
//import cc.mallet.types.SparseVector;
import edu.berkeley.nlp.syntax.Tree;

public class VSMWordFeatureVectorBean implements java.io.Serializable {
	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -2739690989442846855L;

	private String label;
	// private double[] phi;
	private no.uib.cipr.matrix.sparse.SparseVector phi;
	private Tree<String> syntaxTree;
	private Tree<String> insideTree;
	// private double[] psi;
	private no.uib.cipr.matrix.sparse.SparseVector psi;
	private Stack<Tree<String>> footToRoot;

	private int vectorDimensions;

	/*
	 * Storing the inside tree words in this data structure
	 */
	private ArrayList<String> insideTreeWordList;
	/*
	 * Storing the outside tree words in the below data structure
	 */
	private ArrayList<String> outsideTreeWordList;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	// public double[] getPhi() {
	// return phi;
	// }
	public no.uib.cipr.matrix.sparse.SparseVector getPhi() {
		return phi;
	}

	// public void setPhi(double[] phi) {
	// this.phi = phi;
	// }
	public void setPhi(no.uib.cipr.matrix.sparse.SparseVector phi) {
		this.phi = phi;
	}

	public Tree<String> getSyntaxTree() {
		return syntaxTree;
	}

	public void setSyntaxTree(Tree<String> syntaxTree) {
		this.syntaxTree = syntaxTree;
	}

	public Tree<String> getInsideTree() {
		return insideTree;
	}

	public void setInsideTree(Tree<String> insideTree) {
		this.insideTree = insideTree;
	}

	// public double[] getPsi() {
	// return psi;
	// }
	public no.uib.cipr.matrix.sparse.SparseVector getPsi() {
		return psi;
	}

	// public void setPsi(double[] psi) {
	// this.psi = psi;
	// }
	public void setPsi(no.uib.cipr.matrix.sparse.SparseVector psi) {
		this.psi = psi;
	}

	public Stack<Tree<String>> getFootToRoot() {
		return footToRoot;
	}

	public void setFootToRoot(Stack<Tree<String>> footToRoot) {
		this.footToRoot = footToRoot;
	}

	public ArrayList<String> getInsideTreeFeatureList() {
		return insideTreeWordList;
	}

	public void setInsideTreeFeatureList(ArrayList<String> insideTreeFeatureList) {
		this.insideTreeWordList = insideTreeFeatureList;
	}

	public ArrayList<String> getOutsideTreeFeatureList() {
		return outsideTreeWordList;
	}

	public void setOutsideTreeFeatureList(ArrayList<String> outsideTreeFeatureList) {
		this.outsideTreeWordList = outsideTreeFeatureList;
	}

	public int getVectorDimensions() {
		return vectorDimensions;
	}

	public void setVectorDimensions(int vectorDimensions) {
		this.vectorDimensions = vectorDimensions;
	}
}
