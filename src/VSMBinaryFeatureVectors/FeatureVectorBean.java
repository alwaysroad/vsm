package VSMBinaryFeatureVectors;

import java.util.ArrayList;
import java.util.Stack;

import no.uib.cipr.matrix.sparse.SparseVector;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class FeatureVectorBean implements java.io.Serializable {

	private static final long serialVersionUID = -2739690989442846855L;

	private String label;

	private SparseVector phi;
	private Tree<String> syntaxTree;
	private Tree<String> insideTree;

	private SparseVector psi;
	private SparseVector phiSem;
	private SparseVector psiSem;
	private Stack<Tree<String>> footToRoot;
	private int insideFeatureVectorDim;
	private int outsideFeatureVectorDim;
	private ArrayList<String> insideTreeFeatureList;
	private ArrayList<String> outsideTreeFeatureList;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SparseVector getPhi() {
		return phi;
	}

	public void setPhi(SparseVector phi) {
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

	public SparseVector getPsi() {
		return psi;
	}

	public void setPsi(SparseVector psi) {
		this.psi = psi;
	}

	public Stack<Tree<String>> getFootToRoot() {
		return footToRoot;
	}

	public void setFootToRoot(Stack<Tree<String>> footToRoot) {
		this.footToRoot = footToRoot;
	}

	public int getInsideFeatureVectorDim() {
		return insideFeatureVectorDim;
	}

	public void setInsideFeatureVectorDim(int insideFeatureVectorDim) {
		this.insideFeatureVectorDim = insideFeatureVectorDim;
	}

	public int getOutsideFeatureVectorDim() {
		return outsideFeatureVectorDim;
	}

	public void setOutsideFeatureVectorDim(int outsideFeatureVectorDim) {
		this.outsideFeatureVectorDim = outsideFeatureVectorDim;
	}

	public ArrayList<String> getInsideTreeFeatureList() {
		return insideTreeFeatureList;
	}

	public void setInsideTreeFeatureList(ArrayList<String> insideTreeFeatureList) {
		this.insideTreeFeatureList = insideTreeFeatureList;
	}

	public ArrayList<String> getOutsideTreeFeatureList() {
		return outsideTreeFeatureList;
	}

	public void setOutsideTreeFeatureList(
			ArrayList<String> outsideTreeFeatureList) {
		this.outsideTreeFeatureList = outsideTreeFeatureList;
	}

	public SparseVector getPhiSem() {
		return phiSem;
	}

	public void setPhiSem(SparseVector phiSem) {
		this.phiSem = phiSem;
	}

	public SparseVector getPsiSem() {
		return psiSem;
	}

	public void setPsiSem(SparseVector psiSem) {
		this.psiSem = psiSem;
	}
}
