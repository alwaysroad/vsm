package edu.upenn.cis.swell.SpectralRepresentations;

/**
 * ver: 1.0
 * @author paramveer dhillon.
 *
 * last modified: 09/04/13
 * please send bug reports and suggestions to: dhillon@cis.upenn.edu
 */


import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import Jama.Matrix;
import edu.upenn.cis.swell.IO.Options;
import edu.upenn.cis.swell.MainMethods.ContextPCA;
import edu.upenn.cis.swell.MathUtils.CenterScaleNormalizeUtils;

public abstract class SpectralRepresentation implements Serializable{
	
	protected int _num_hidden=50; // the number of hidden states (k), the lower dimensional embedding
	private int _vocab_size=30000; // what is this? isn't the vocab size equal to the total number of tokens?!
	protected long _num_tokens=-1;
	static final long serialVersionUID = 42L;
	protected Options _opt;
	protected Matrix eigenFeatDictMatrix;
	CenterScaleNormalizeUtils mathUtils;
	
	public SpectralRepresentation(Options opt, long numTok){
		_opt=opt; // command line options
		_num_hidden=_opt.hiddenStateSize; //this is what we gave at the input
		_vocab_size=_opt.vocabSize; //100? what is this
		_num_tokens=numTok; // total number of tokens
		
		mathUtils=new CenterScaleNormalizeUtils(_opt); // just getting the mathutils upbject in order to form the sparse matrices and all
		
		initialize();
	}
	
	protected void initialize(){
		
		Random r= new Random();
		double[][] eigenFeatDict= new double[_vocab_size+1][_num_hidden]; // Okay makes sense, it is v \times k basically, Intializind the data structure that will hold the embeddings. In my case the number of rows would be N(Number of samples of a node) and k would be dimensions, so N \times K
		for (int i=0;i<_vocab_size+1;i++){ //Randomly initializing the data structure
			for (int j=0;j<_num_hidden;j++) // So, i will also randomly initialize the eigen dictionary
				eigenFeatDict[i][j]=r.nextGaussian(); // Randomly intializing each variable
		}
		
		/*
		try {
			ContextPCA.embedMatrixProcess(_opt);
			eigenFeatDictMatrix=ContextPCA.getEmbedMatrix();
			System.out.println("A matrix initialized");
			//eigenFeatDictMatrix=ContextPCA.getwordDict()();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		eigenFeatDictMatrix= new Matrix(eigenFeatDict); //v times k matrix, N \times K in my case where is the number of node types
		eigenFeatDictMatrix=mathUtils.center_and_scale(eigenFeatDictMatrix); // center and scale the feature dictionary matrix, all the elements normalized and centered to have zero mean
	}
	
	
		
	public Matrix getEigenFeatDict(){
		return eigenFeatDictMatrix;
	}
	
	public void setEigenFeatDict(Matrix eigenFeatDictMatrix){
		this.eigenFeatDictMatrix=eigenFeatDictMatrix;
	}
}


