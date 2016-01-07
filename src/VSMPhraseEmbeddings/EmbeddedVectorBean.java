package VSMPhraseEmbeddings;

import java.util.ArrayList;
import java.util.Stack;

import no.uib.cipr.matrix.DenseVector;
import edu.berkeley.nlp.syntax.Tree;

public class EmbeddedVectorBean implements java.io.Serializable {
	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -2739690989442846855L;

	private String label;

	private DenseVector phi;

	private DenseVector psi;
	private DenseVector phiSem;
	private DenseVector psiSem;
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

	public DenseVector getPhiEmbedded() {
		return phi;
	}

	public void setPhiSynEmbedded(DenseVector phi) {
		this.phi = phi;
	}

	public DenseVector getPsiEmbedded() {
		return psi;
	}

	public void setPsiSynEmbedded(DenseVector psi) {
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

	public DenseVector getPhiSem() {
		return phiSem;
	}

	public void setPhiSemEmbedded(DenseVector phiSem) {
		this.phiSem = phiSem;
	}

	public DenseVector getPsiSem() {
		return psiSem;
	}

	public void setPsiSemEmbedded(DenseVector psiSem) {
		this.psiSem = psiSem;
	}
}
