package VSMSICKSentenceEmbeddingsSyntactic;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMFeatureVectorBeanEmbedded;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceVecBean;
import VSMSerialization.VSMSerializeSentenceVectorBean;
import VSMUtilityClasses.VSMUtil;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class SentenceEmbeddingSyntactic {

	private static final Logger LOGGER;

	private static File[] sentences;

	private static Vector sentenceEmbedding;

	private static int nonTerminals;

	private static Vector[] embeddedFeatureVectors;

	private static File[] embeddedChunkVectors;

	private static File[] chunks;

	private static Vector ccinSyn;
	private static Vector ccoutSyn;

	private static Vector nninSyn;
	private static Vector nnoutSyn;

	private static Vector ininSyn;
	private static Vector inoutSyn;

	private static Vector jjinSyn;
	private static Vector jjoutSyn;

	private static Vector nnpinSyn;
	private static Vector nnpoutSyn;

	private static Vector nnsinSyn;
	private static Vector nnsoutSyn;

	private static Vector prpinSyn;
	private static Vector prpoutSyn;

	private static Vector sbarinSyn;
	private static Vector sbaroutSyn;

	private static Vector vbpinSyn;
	private static Vector vbpoutSyn;

	private static Vector vbninSyn;
	private static Vector vbnoutSyn;

	private static Vector vbginSyn;
	private static Vector vbgoutSyn;

	private static Vector rbinSyn;
	private static Vector rboutSyn;

	private static Vector sinSyn;
	private static Vector soutSyn;

	private static Vector npinSyn;
	private static Vector npoutSyn;

	private static Vector vpinSyn;
	private static Vector vpoutSyn;

	private static Vector ppinSyn;
	private static Vector ppoutSyn;

	private static VSMSentenceVecBean vecBean;

	static {

		LOGGER = VSMLogger.setup(SentenceEmbeddingSyntactic.class.getName());
	}

	public static void main(String[] args) {

		nonTerminals = VSMUtil.getNumberOfNonTerminals(args, LOGGER);

		sentences = VSMUtil
				.getSICKSentenceDirectories(VSMContant.SICK_EMBEDDED_FEATURE_VECS);

		sortByNumber(sentences);

		for (File sentenceDirectory : sentences) {

			sentenceEmbedding = new DenseVector(nonTerminals * 100);

			embeddedFeatureVectors = new DenseVector[nonTerminals * 2];

			chunks = VSMUtil.getSentenceChunks(sentenceDirectory, LOGGER);

			setUp();

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
		vecBean = new VSMSentenceVecBean();

		vecBean.setSentenceVector((DenseVector) sentenceEmbedding);

		VSMSerializeSentenceVectorBean.sentenceVectorBeanSyntactic(vecBean,
				sentenceDirectory.getName());

	}

	/*
	 * 
	 */
	private static void formSenetenceEmbedding() {
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
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vbninSyn = vbninSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vbnoutSyn = vbnoutSyn.add(vectorBean.getPsiEmbedded());

		}

		vbninSyn = vbninSyn.scale((double) 1 / count);
		vbnoutSyn = vbnoutSyn.scale((double) 1 / count);

	}

	private static void embedVBP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vbpinSyn = vbpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vbpoutSyn = vbpoutSyn.add(vectorBean.getPsiEmbedded());

		}

		vbpinSyn = vbpinSyn.scale((double) 1 / count);
		vbpoutSyn = vbpoutSyn.scale((double) 1 / count);

	}

	private static void embedVBG() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vbginSyn = vbginSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vbgoutSyn = vbgoutSyn.add(vectorBean.getPsiEmbedded());

		}

		vbginSyn = vbginSyn.scale((double) 1 / count);
		vbgoutSyn = vbgoutSyn.scale((double) 1 / count);

	}

	private static void embedS() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				sinSyn = sinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				soutSyn = soutSyn.add(vectorBean.getPsiEmbedded());

		}

		sinSyn = sinSyn.scale((double) 1 / count);
		soutSyn = soutSyn.scale((double) 1 / count);

	}

	private static void embedPP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				ppinSyn = ppinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				ppoutSyn = ppoutSyn.add(vectorBean.getPsiEmbedded());

		}

		ppinSyn = ppinSyn.scale((double) 1 / count);
		ppoutSyn = ppoutSyn.scale((double) 1 / count);

	}

	private static void embedVP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				vpinSyn = vpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				vpoutSyn = vpoutSyn.add(vectorBean.getPsiEmbedded());

		}

		vpinSyn = vpinSyn.scale((double) 1 / count);
		vpoutSyn = vpoutSyn.scale((double) 1 / count);

	}

	private static void embedNP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				npinSyn = npinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				npoutSyn = npoutSyn.add(vectorBean.getPsiEmbedded());

		}

		npinSyn = npinSyn.scale((double) 1 / count);
		npoutSyn = npoutSyn.scale((double) 1 / count);

	}

	private static void embedRB() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				rbinSyn = rbinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				rboutSyn = rboutSyn.add(vectorBean.getPsiEmbedded());

		}

		rbinSyn = rbinSyn.scale((double) 1 / count);
		rboutSyn = rboutSyn.scale((double) 1 / count);

	}

	private static void embedSBAR() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				sbarinSyn = sbarinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				sbaroutSyn = sbaroutSyn.add(vectorBean.getPsiEmbedded());

		}

		sbarinSyn = sbarinSyn.scale((double) 1 / count);
		sbaroutSyn = sbaroutSyn.scale((double) 1 / count);

	}

	private static void embedPRP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				prpinSyn = prpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				prpoutSyn = prpoutSyn.add(vectorBean.getPsiEmbedded());

		}

		prpinSyn = prpinSyn.scale((double) 1 / count);
		prpoutSyn = prpoutSyn.scale((double) 1 / count);

	}

	private static void embedNNS() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				nnsinSyn = nnsinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				nnsoutSyn = nnsoutSyn.add(vectorBean.getPsiEmbedded());

		}

		nnsinSyn = nnsinSyn.scale((double) 1 / count);
		nnsoutSyn = nnsoutSyn.scale((double) 1 / count);

	}

	private static void embedNNP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				nnpinSyn = nnpinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				nnpoutSyn = nnpoutSyn.add(vectorBean.getPsiEmbedded());

		}

		nnpinSyn = nnpinSyn.scale((double) 1 / count);
		nnpoutSyn = nnpoutSyn.scale((double) 1 / count);

	}

	private static void embedJJ() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				jjinSyn = jjinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				jjoutSyn = jjoutSyn.add(vectorBean.getPsiEmbedded());

		}

		jjinSyn = jjinSyn.scale((double) 1 / count);
		jjoutSyn = jjoutSyn.scale((double) 1 / count);

	}

	private static void embedIN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				ininSyn = ininSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				inoutSyn = inoutSyn.add(vectorBean.getPsiEmbedded());

		}

		ininSyn = ininSyn.scale((double) 1 / count);
		inoutSyn = inoutSyn.scale((double) 1 / count);

	}

	private static void embedNN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());
			if (vectorBean.getPhiEmbedded() != null)
				nninSyn = nninSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				nnoutSyn = nnoutSyn.add(vectorBean.getPsiEmbedded());

		}

		nninSyn = nninSyn.scale((double) 1 / count);
		nnoutSyn = nnoutSyn.scale((double) 1 / count);

	}

	private static void embedCC() {

		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
					.readFeatureVectorBeanEmbedded(embeddeVec.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				ccinSyn = ccinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				ccoutSyn = ccoutSyn.add(vectorBean.getPsiEmbedded());

		}

		ccinSyn = ccinSyn.scale((double) 1 / count);
		ccoutSyn = ccoutSyn.scale((double) 1 / count);

	}

	private static void setUp() {

		ccinSyn = new DenseVector(50);
		ccoutSyn = new DenseVector(50);

		nninSyn = new DenseVector(50);
		nnoutSyn = new DenseVector(50);

		ininSyn = new DenseVector(50);
		inoutSyn = new DenseVector(50);

		jjinSyn = new DenseVector(50);
		jjoutSyn = new DenseVector(50);

		nnpinSyn = new DenseVector(50);
		nnpoutSyn = new DenseVector(50);

		nnsinSyn = new DenseVector(50);
		nnsoutSyn = new DenseVector(50);

		prpinSyn = new DenseVector(50);
		prpoutSyn = new DenseVector(50);

		sbarinSyn = new DenseVector(50);
		sbaroutSyn = new DenseVector(50);

		vbpinSyn = new DenseVector(50);
		vbpoutSyn = new DenseVector(50);

		vbninSyn = new DenseVector(50);
		vbnoutSyn = new DenseVector(50);

		vbginSyn = new DenseVector(50);
		vbgoutSyn = new DenseVector(50);

		rbinSyn = new DenseVector(50);
		rboutSyn = new DenseVector(50);

		sinSyn = new DenseVector(50);
		soutSyn = new DenseVector(50);

		npinSyn = new DenseVector(50);
		npoutSyn = new DenseVector(50);

		vpinSyn = new DenseVector(50);
		vpoutSyn = new DenseVector(50);

		ppinSyn = new DenseVector(50);
		ppoutSyn = new DenseVector(50);

		embeddedFeatureVectors[0] = ininSyn;
		embeddedFeatureVectors[1] = inoutSyn;

		embeddedFeatureVectors[2] = nninSyn;
		embeddedFeatureVectors[3] = nnoutSyn;

		embeddedFeatureVectors[4] = ccinSyn;
		embeddedFeatureVectors[5] = ccoutSyn;

		embeddedFeatureVectors[6] = jjinSyn;
		embeddedFeatureVectors[7] = jjoutSyn;

		embeddedFeatureVectors[8] = nnpinSyn;
		embeddedFeatureVectors[9] = nnpoutSyn;

		embeddedFeatureVectors[10] = nnsinSyn;
		embeddedFeatureVectors[11] = nnsoutSyn;

		embeddedFeatureVectors[12] = prpinSyn;
		embeddedFeatureVectors[13] = prpoutSyn;

		embeddedFeatureVectors[14] = sbarinSyn;
		embeddedFeatureVectors[15] = sbaroutSyn;

		embeddedFeatureVectors[16] = vbpinSyn;
		embeddedFeatureVectors[17] = vbpoutSyn;

		embeddedFeatureVectors[18] = rbinSyn;
		embeddedFeatureVectors[19] = rboutSyn;

		embeddedFeatureVectors[20] = sinSyn;
		embeddedFeatureVectors[21] = soutSyn;

		embeddedFeatureVectors[22] = vbginSyn;
		embeddedFeatureVectors[23] = vbgoutSyn;

		embeddedFeatureVectors[24] = vbninSyn;
		embeddedFeatureVectors[25] = vbnoutSyn;

		embeddedFeatureVectors[26] = npinSyn;
		embeddedFeatureVectors[27] = npoutSyn;

		embeddedFeatureVectors[28] = vpinSyn;
		embeddedFeatureVectors[29] = vpoutSyn;

		embeddedFeatureVectors[30] = ppinSyn;
		embeddedFeatureVectors[31] = ppoutSyn;

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
}
