package VSMSentenceEmbeddingFull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMSICKEmbeddedFeatureVecs.EmbeddedVectorBean;
import VSMUtilityClasses.VSMUtil;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class SentenceEmbeddingTRIAL {

	private static Logger LOGGER;

	private static File[] sentences;

	private static Vector sentenceEmbedding;

	private static int nonTerminals;

	private static Vector[] embeddedFeatureVectors;

	private static File[] embeddedChunkVectors;

	private static File[] chunks;

	private static SentenceVectorBean vecBean;

	private static Vector ccinSyn;
	private static Vector ccoutSyn;
	private static Vector ccinSem;
	private static Vector ccoutSem;

	private static Vector nninSyn;
	private static Vector nnoutSyn;
	private static Vector nninSem;
	private static Vector nnoutSem;

	private static Vector ininSyn;
	private static Vector inoutSyn;
	private static Vector ininSem;
	private static Vector inoutSem;

	private static Vector jjinSyn;
	private static Vector jjoutSyn;
	private static Vector jjinSem;
	private static Vector jjoutSem;

	private static Vector nnpinSyn;
	private static Vector nnpoutSyn;
	private static Vector nnpinSem;
	private static Vector nnpoutSem;

	private static Vector nnsinSyn;
	private static Vector nnsoutSyn;
	private static Vector nnsinSem;
	private static Vector nnsoutSem;

	private static Vector prpinSyn;
	private static Vector prpoutSyn;
	private static Vector prpinSem;
	private static Vector prpoutSem;

	private static Vector sbarinSyn;
	private static Vector sbaroutSyn;
	private static Vector sbarinSem;
	private static Vector sbaroutSem;

	private static Vector vbpinSyn;
	private static Vector vbpoutSyn;
	private static Vector vbpinSem;
	private static Vector vbpoutSem;

	private static Vector vbninSyn;
	private static Vector vbnoutSyn;
	private static Vector vbninSem;
	private static Vector vbnoutSem;

	private static Vector vbginSyn;
	private static Vector vbgoutSyn;
	private static Vector vbginSem;
	private static Vector vbgoutSem;

	private static Vector rbinSyn;
	private static Vector rboutSyn;
	private static Vector rbinSem;
	private static Vector rboutSem;

	private static Vector sinSyn;
	private static Vector soutSyn;
	private static Vector sinSem;
	private static Vector soutSem;

	private static Vector npinSyn;
	private static Vector npoutSyn;
	private static Vector npinSem;
	private static Vector npoutSem;

	private static Vector vpinSyn;
	private static Vector vpoutSyn;
	private static Vector vpinSem;
	private static Vector vpoutSem;

	private static Vector ppinSyn;
	private static Vector ppoutSyn;
	private static Vector ppinSem;
	private static Vector ppoutSem;

	private static Vector dtinSyn;

	private static Vector dtoutSyn;

	private static Vector dtinSem;

	private static Vector dtoutSem;

	private static DenseVector vbdinSyn;

	private static DenseVector vbdoutSyn;

	private static DenseVector vbdinSem;

	private static DenseVector vbdoutSem;

	static {
		LOGGER = VSMLogger.setup(SentenceEmbeddingTRIAL.class.getName());
	}

	public static void main(String[] args) {

		nonTerminals = VSMUtil.getNumberOfNonTerminals(args, LOGGER);

		if (nonTerminals == -1) {
			System.out
					.println("PLEASE GIVE THE ARGUMENT FOR THE NUMBER OF NON TERMINALS AT COMMAND LINE");
		}

		sentences = VSMUtil
				.getSICKSentenceDirectories(VSMContant.SICK_TRIAL_EMBEDDED_CHUNK_VECS);

		sortByNumber(sentences);

		for (File sentenceDirectory : sentences) {

			System.out.println(sentenceDirectory);
			// TODO Change this
			sentenceEmbedding = new DenseVector(36000);

			embeddedFeatureVectors = new DenseVector[72];

			setup();

			chunks = VSMUtil.getSentenceChunks(sentenceDirectory, LOGGER);

			for (File chunk : chunks) {

				embeddedChunkVectors = VSMUtil.getEmbeddedChunkVecs(chunk,
						LOGGER);

				String chunkName = chunk.getName();

				if (embeddedChunkVectors != null) {

					switch (chunkName) {

					case "CC":
						embedCC();

						break;

					case "NN":
						embedNN();

						break;

					case "IN":
						embedIN();
						break;

					case "JJ":
						embedJJ();
						break;

					case "NNP":
						embedNNP();
						break;

					case "NNS":
						embedNNS();
						break;

					case "PRP":
						embedPRP();
						break;

					case "SBAR":
						embedSBAR();
						break;

					case "RB":
						embedRB();
						break;

					case "NP":
						embedNP();
						break;

					case "VP":
						embedVP();
						break;

					case "PP":
						embedPP();
						break;

					case "S":
						embedS();
						break;

					case "VBG":
						embedVBG();
						break;

					case "VBP":
						embedVBP();
						break;

					case "VBN":
						embedVBN();
						break;

					default:
						break;

					}

				}
			}

			formSenetenceEmbedding();

			serializeSentenceEmbedding(sentenceDirectory);
		}

	}

	private static void serializeSentenceEmbedding(File sentenceDirectory) {

		vecBean = new SentenceVectorBean();

		vecBean.setSentenceVector((DenseVector) sentenceEmbedding);

		serializeSentenceVector(vecBean, sentenceDirectory.getName());

	}

	private static void formSenetenceEmbedding() {

		int i = 0;
		for (Vector vec : embeddedFeatureVectors) {
			i = i + vec.size();

		}

		int startIdx = 0;
		Iterator<VectorEntry> iterator = null;
		for (Vector vector : embeddedFeatureVectors) {

			iterator = vector.iterator();
			while (iterator.hasNext()) {
				VectorEntry e = iterator.next();
				int idx = e.index() + startIdx;
				double val = e.get();
				sentenceEmbedding.add(idx, val);
			}

			startIdx = startIdx + vector.size();
		}

	}

	private static void embedVBN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vbninSyn = vbninSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vbnoutSyn = vbnoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				vbninSem = vbninSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				vbnoutSem = vbnoutSem.add(vectorBean.getPsiSem());

		}

		vbninSyn = vbninSyn.scale((double) 1 / count);
		vbnoutSyn = vbnoutSyn.scale((double) 1 / count);

		vbninSem = vbninSem.scale((double) 1 / count);
		vbnoutSem = vbnoutSem.scale((double) 1 / count);

	}

	private static void embedVBP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vbpinSyn = vbpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vbpoutSyn = vbpoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				vbpinSem = vbpinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				vbpoutSem = vbpoutSem.add(vectorBean.getPsiSem());

		}

		vbpinSyn = vbpinSyn.scale((double) 1 / count);
		vbpoutSyn = vbpoutSyn.scale((double) 1 / count);

		vbpinSem = vbpinSem.scale((double) 1 / count);
		vbpoutSem = vbpoutSem.scale((double) 1 / count);

	}

	private static void embedVBG() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vbginSyn = vbginSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vbgoutSyn = vbgoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				vbginSem = vbginSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				vbgoutSem = vbgoutSem.add(vectorBean.getPsiSem());

		}

		vbginSyn = vbginSyn.scale((double) 1 / count);
		vbgoutSyn = vbgoutSyn.scale((double) 1 / count);

		vbginSem = vbginSem.scale((double) 1 / count);
		vbgoutSem = vbgoutSem.scale((double) 1 / count);

	}

	private static void embedS() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				sinSyn = sinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				soutSyn = soutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				sinSem = sinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				soutSem = soutSem.add(vectorBean.getPsiSem());

		}

		sinSyn = sinSyn.scale((double) 1 / count);
		soutSyn = soutSyn.scale((double) 1 / count);

		sinSem = sinSem.scale((double) 1 / count);
		soutSem = soutSem.scale((double) 1 / count);

	}

	private static void embedPP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				ppinSyn = ppinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				ppoutSyn = ppoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				ppinSem = ppinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				ppoutSem = ppoutSem.add(vectorBean.getPsiSem());

		}

		ppinSyn = ppinSyn.scale((double) 1 / count);
		ppoutSyn = ppoutSyn.scale((double) 1 / count);

		ppinSem = ppinSem.scale((double) 1 / count);
		ppoutSem = ppoutSem.scale((double) 1 / count);

	}

	private static void embedVP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vpinSyn = vpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vpoutSyn = vpoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				vpinSem = vpinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				vpoutSem = vpoutSem.add(vectorBean.getPsiSem());

		}

		vpinSyn = vpinSyn.scale((double) 1 / count);
		vpoutSyn = vpoutSyn.scale((double) 1 / count);

		vpinSem = vpinSem.scale((double) 1 / count);
		vpoutSem = vpoutSem.scale((double) 1 / count);

	}

	private static void embedNP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				npinSyn = npinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				npoutSyn = npoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				npinSem = npinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				npoutSem = npoutSem.add(vectorBean.getPsiSem());

		}

		npinSyn = npinSyn.scale((double) 1 / count);
		npoutSyn = npoutSyn.scale((double) 1 / count);

		npinSem = npinSem.scale((double) 1 / count);
		npoutSem = npoutSem.scale((double) 1 / count);

	}

	private static void embedRB() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				rbinSyn = rbinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				rboutSyn = rboutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				rbinSem = rbinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				rboutSem = rboutSem.add(vectorBean.getPsiSem());

		}

		rbinSyn = rbinSyn.scale((double) 1 / count);
		rboutSyn = rboutSyn.scale((double) 1 / count);

		rbinSem = rbinSem.scale((double) 1 / count);
		rboutSem = rboutSem.scale((double) 1 / count);

	}

	private static void embedSBAR() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				sbarinSyn = sbarinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				sbaroutSyn = sbaroutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				sbarinSem = sbarinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				sbaroutSem = sbaroutSem.add(vectorBean.getPsiSem());

		}

		sbarinSyn = sbarinSyn.scale((double) 1 / count);
		sbaroutSyn = sbaroutSyn.scale((double) 1 / count);
		sbarinSem = sbarinSem.scale((double) 1 / count);
		sbaroutSem = sbaroutSem.scale((double) 1 / count);

	}

	private static void embedPRP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				prpinSyn = prpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				prpoutSyn = prpoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				prpinSem = prpinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				prpoutSem = prpoutSem.add(vectorBean.getPsiSem());

		}

		prpinSyn = prpinSyn.scale((double) 1 / count);
		prpoutSyn = prpoutSyn.scale((double) 1 / count);
		prpinSem = prpinSem.scale((double) 1 / count);
		prpoutSem = prpoutSem.scale((double) 1 / count);

	}

	private static void embedNNS() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				nnsinSyn = nnsinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				nnsoutSyn = nnsoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				nnsinSem = nnsinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				nnsoutSem = nnsoutSem.add(vectorBean.getPsiSem());

		}

		nnsinSyn = nnsinSyn.scale((double) 1 / count);
		nnsoutSyn = nnsoutSyn.scale((double) 1 / count);

		nnsinSem = nnsinSem.scale((double) 1 / count);
		nnsoutSem = nnsoutSem.scale((double) 1 / count);

	}

	private static void embedNNP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				nnpinSyn = nnpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				nnpoutSyn = nnpoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				nnpinSem = nnpinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				nnpoutSem = nnpoutSem.add(vectorBean.getPsiSem());

		}

		nnpinSyn = nnpinSyn.scale((double) 1 / count);
		nnpoutSyn = nnpoutSyn.scale((double) 1 / count);

		nnpinSem = nnpinSem.scale((double) 1 / count);
		nnpoutSem = nnpoutSem.scale((double) 1 / count);

	}

	private static void embedJJ() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				jjinSyn = jjinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				jjoutSyn = jjoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				jjinSem = jjinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				jjoutSem = jjoutSem.add(vectorBean.getPsiSem());

		}

		jjinSyn = jjinSyn.scale((double) 1 / count);
		jjoutSyn = jjoutSyn.scale((double) 1 / count);

		jjinSem = jjinSem.scale((double) 1 / count);
		jjoutSem = jjoutSem.scale((double) 1 / count);

	}

	private static void embedIN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				ininSyn = ininSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				inoutSyn = inoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				ininSem = ininSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				inoutSem = inoutSem.add(vectorBean.getPsiSem());

		}

		ininSyn = ininSyn.scale((double) 1 / count);
		inoutSyn = inoutSyn.scale((double) 1 / count);
		ininSem = ininSem.scale((double) 1 / count);
		inoutSem = inoutSem.scale((double) 1 / count);

	}

	private static void embedNN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean.getPhiEmbedded() != null)
				nninSyn = nninSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				nnoutSyn = nnoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				nninSem = nninSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				nnoutSem = nnoutSem.add(vectorBean.getPsiSem());

		}

		nninSyn = nninSyn.scale((double) 1 / count);
		nnoutSyn = nnoutSyn.scale((double) 1 / count);
		nninSem = nninSem.scale((double) 1 / count);
		nnoutSem = nnoutSem.scale((double) 1 / count);

	}

	private static void embedCC() {

		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				ccinSyn = ccinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				ccoutSyn = ccoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				ccinSem = ccinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				ccoutSem = ccoutSem.add(vectorBean.getPsiSem());

		}

		ccinSyn = ccinSyn.scale((double) 1 / count);
		ccoutSyn = ccoutSyn.scale((double) 1 / count);

		ccinSem = ccinSem.scale((double) 1 / count);
		ccoutSem = ccoutSem.scale((double) 1 / count);

	}

	private static void setup() {

		ccinSyn = new DenseVector(30);
		ccoutSyn = new DenseVector(30);
		ccinSem = new DenseVector(14);
		ccoutSem = new DenseVector(14);

		nninSyn = new DenseVector(640);
		nnoutSyn = new DenseVector(640);
		nninSem = new DenseVector(640);
		nnoutSem = new DenseVector(640);

		ininSyn = new DenseVector(96);
		inoutSyn = new DenseVector(96);
		ininSem = new DenseVector(37);
		inoutSem = new DenseVector(37);

		jjinSyn = new DenseVector(640);
		jjoutSyn = new DenseVector(640);
		jjinSem = new DenseVector(100);
		jjoutSem = new DenseVector(100);

		nnpinSyn = new DenseVector(640);
		nnpoutSyn = new DenseVector(640);
		nnpinSem = new DenseVector(640);
		nnpoutSem = new DenseVector(640);

		nnsinSyn = new DenseVector(640);
		nnsoutSyn = new DenseVector(640);
		nnsinSem = new DenseVector(640);
		nnsoutSem = new DenseVector(640);

		prpinSyn = new DenseVector(42);
		prpoutSyn = new DenseVector(42);
		prpinSem = new DenseVector(13);
		prpoutSem = new DenseVector(13);

		sbarinSyn = new DenseVector(640);
		sbaroutSyn = new DenseVector(640);
		sbarinSem = new DenseVector(640);
		sbaroutSem = new DenseVector(640);

		vbpinSyn = new DenseVector(640);
		vbpoutSyn = new DenseVector(640);
		vbpinSem = new DenseVector(640);
		vbpoutSem = new DenseVector(640);

		vbninSyn = new DenseVector(640);
		vbnoutSyn = new DenseVector(640);
		vbninSem = new DenseVector(640);
		vbnoutSem = new DenseVector(640);

		vbginSyn = new DenseVector(640);
		vbgoutSyn = new DenseVector(640);
		vbginSem = new DenseVector(640);
		vbgoutSem = new DenseVector(640);

		rbinSyn = new DenseVector(640);
		rboutSyn = new DenseVector(640);
		rbinSem = new DenseVector(640);
		rboutSem = new DenseVector(640);

		sinSyn = new DenseVector(640);
		soutSyn = new DenseVector(640);
		sinSem = new DenseVector(640);
		soutSem = new DenseVector(640);

		npinSyn = new DenseVector(640);
		npoutSyn = new DenseVector(640);
		npinSem = new DenseVector(640);
		npoutSem = new DenseVector(640);

		vpinSyn = new DenseVector(640);
		vpoutSyn = new DenseVector(640);
		vpinSem = new DenseVector(640);
		vpoutSem = new DenseVector(640);

		ppinSyn = new DenseVector(640);
		ppoutSyn = new DenseVector(640);
		ppinSem = new DenseVector(640);
		ppoutSem = new DenseVector(640);

		dtinSyn = new DenseVector(10);
		dtoutSyn = new DenseVector(10);
		dtinSem = new DenseVector(4);
		dtoutSem = new DenseVector(4);

		vbdinSyn = new DenseVector(640);
		vbdoutSyn = new DenseVector(640);
		vbdinSem = new DenseVector(640);
		vbdoutSem = new DenseVector(640);

		embeddedFeatureVectors[0] = ininSyn;
		embeddedFeatureVectors[1] = inoutSyn;
		embeddedFeatureVectors[2] = ininSem;
		embeddedFeatureVectors[3] = inoutSem;

		embeddedFeatureVectors[4] = nninSyn;
		embeddedFeatureVectors[5] = nnoutSyn;
		embeddedFeatureVectors[6] = nninSem;
		embeddedFeatureVectors[7] = nnoutSem;

		embeddedFeatureVectors[8] = ccinSyn;
		embeddedFeatureVectors[9] = ccoutSyn;
		embeddedFeatureVectors[10] = ccinSem;
		embeddedFeatureVectors[11] = ccoutSem;

		embeddedFeatureVectors[12] = jjinSyn;
		embeddedFeatureVectors[13] = jjoutSyn;
		embeddedFeatureVectors[14] = jjinSem;
		embeddedFeatureVectors[15] = jjoutSem;

		embeddedFeatureVectors[16] = nnpinSyn;
		embeddedFeatureVectors[17] = nnpoutSyn;
		embeddedFeatureVectors[18] = nnpinSem;
		embeddedFeatureVectors[19] = nnpoutSem;

		embeddedFeatureVectors[20] = nnsinSyn;
		embeddedFeatureVectors[21] = nnsoutSyn;
		embeddedFeatureVectors[22] = nnsinSem;
		embeddedFeatureVectors[23] = nnsoutSem;

		embeddedFeatureVectors[24] = prpinSyn;
		embeddedFeatureVectors[25] = prpoutSyn;
		embeddedFeatureVectors[26] = prpinSem;
		embeddedFeatureVectors[27] = prpoutSem;

		embeddedFeatureVectors[28] = sbarinSyn;
		embeddedFeatureVectors[29] = sbaroutSyn;
		embeddedFeatureVectors[30] = sbarinSem;
		embeddedFeatureVectors[31] = sbaroutSem;

		embeddedFeatureVectors[32] = vbpinSyn;
		embeddedFeatureVectors[33] = vbpoutSyn;
		embeddedFeatureVectors[34] = vbpinSem;
		embeddedFeatureVectors[35] = vbpoutSem;

		embeddedFeatureVectors[36] = rbinSyn;
		embeddedFeatureVectors[37] = rboutSyn;
		embeddedFeatureVectors[38] = rbinSem;
		embeddedFeatureVectors[39] = rboutSem;

		embeddedFeatureVectors[40] = sinSyn;
		embeddedFeatureVectors[41] = soutSyn;
		embeddedFeatureVectors[42] = sinSem;
		embeddedFeatureVectors[43] = soutSem;

		embeddedFeatureVectors[44] = vbginSyn;
		embeddedFeatureVectors[45] = vbgoutSyn;
		embeddedFeatureVectors[46] = vbginSem;
		embeddedFeatureVectors[47] = vbgoutSem;

		embeddedFeatureVectors[48] = vbninSyn;
		embeddedFeatureVectors[49] = vbnoutSyn;
		embeddedFeatureVectors[50] = vbninSem;
		embeddedFeatureVectors[51] = vbnoutSem;

		embeddedFeatureVectors[52] = npinSyn;
		embeddedFeatureVectors[53] = npoutSyn;
		embeddedFeatureVectors[54] = npinSem;
		embeddedFeatureVectors[55] = npoutSem;

		embeddedFeatureVectors[56] = vpinSyn;
		embeddedFeatureVectors[57] = vpoutSyn;
		embeddedFeatureVectors[58] = vpinSem;
		embeddedFeatureVectors[59] = vpoutSem;

		embeddedFeatureVectors[60] = ppinSyn;
		embeddedFeatureVectors[61] = ppoutSyn;
		embeddedFeatureVectors[62] = ppinSem;
		embeddedFeatureVectors[63] = ppoutSem;

		embeddedFeatureVectors[64] = dtinSyn;
		embeddedFeatureVectors[65] = dtoutSyn;
		embeddedFeatureVectors[66] = dtinSem;
		embeddedFeatureVectors[67] = dtoutSem;

		embeddedFeatureVectors[68] = vbdinSyn;
		embeddedFeatureVectors[69] = vbdoutSyn;
		embeddedFeatureVectors[70] = vbdinSem;
		embeddedFeatureVectors[71] = vbdoutSem;

	}

	public static void sortByNumber(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				int n1 = extractNumer(o1.getName());
				int n2 = extractNumer(o2.getName());
				return n1 - n2;
			}

			private int extractNumer(String name) {

				int i = 0;

				try {
					int s = name.indexOf('_') + 1;
					int e = name.lastIndexOf('.');
					String number = name.substring(s, e);
					i = Integer.parseInt(number);
				} catch (Exception e) {
					i = 0;
				}
				return i;

			}
		});
	}

	public static void serializeSentenceVector(SentenceVectorBean vecBean,
			String sentence) {

		File file = new File(VSMContant.SICK_TRIAL_EMBEDDINGS_SENT);
		if (!file.exists()) {
			file.mkdir();
		}

		String filename = file.getAbsolutePath() + "/" + sentence + ".ser";

		File sentenceVecFile = new File(filename);
		if (sentenceVecFile.exists()) {
			file.delete();
		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			out = new ObjectOutputStream(fos);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static EmbeddedVectorBean readFeatureVectorBeanEmbedded(
			String fileName) {

		EmbeddedVectorBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (EmbeddedVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}
}
