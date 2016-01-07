package VSMMain;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import weka.core.Stopwords;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import VSMConstants.VSMContant;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceEmbeddingBean;
import VSMSerialization.VSMSerializeSentenceEmbedding;
import VSMSerialization.VSMWordEmbeddingMapBean;
import VSMUtilityClasses.VSMUtil;
import no.uib.cipr.matrix.DenseVector;

import org.apache.commons.lang3.StringUtils;

public class VSMSentenceVectorsAveragingTrial {

	public static void main(String... args) {

		String parseTreeCorpus = VSMContant.SICK_TRIAL_TREES;
		String fileName = VSMContant.CCA_WORD_EMBEDDING_OBJ;

		VSMWordEmbeddingMapBean mapBean = VSMReadSerialObject
				.readWordEmbeddingMapBean(fileName);

		HashMap<String, double[]> embeddingsMap = mapBean.getEmbeddingsMap();
		System.out.println(embeddingsMap.keySet().size());
		System.out.println(embeddingsMap.get("soccer"));

		PennTreeReader treeReader = null;

		try {
			treeReader = VSMUtil.getTreeReader(parseTreeCorpus);
		} catch (Exception e) {
			System.out.println("Exception while reading the SICK trees" + e);
		}

		int sentenceCount = 0;
		while (treeReader.hasNext()) {
			sentenceCount++;
			Tree<String> tree = treeReader.next();

			List<String> sentenceTokens = tree.getTerminalYield();

			// Filtering the tokens

			VSMUtil.lowercase(sentenceTokens);
			VSMUtil.normalize(sentenceTokens);

			Iterator<String> listItr = sentenceTokens.iterator();

			while (listItr.hasNext()) {
				String token = listItr.next();
				if (Stopwords.isStopword(token)) {
					listItr.remove();
				}

				if (!StringUtils.isAlphanumeric(token)) {
					try {
						if (!Character.isLetterOrDigit(token.charAt(0))) {
							listItr.remove();
						}
					} catch (StringIndexOutOfBoundsException e) {
						System.out
								.println("**Catching the Exception and moving on***"
										+ e);
					}
				}
			}

			// Get the sentence embedding
			DenseVector sentenceEmbedding = new DenseVector(
					VSMContant.CCA_WORD_EMBED_SIZE);

			for (String token : sentenceTokens) {
				// TODO Get the word embedding for the token
				if (embeddingsMap.containsKey(token.toLowerCase())
						|| embeddingsMap.containsKey(token.toUpperCase())) {
					double[] embedding = embeddingsMap.get(token.toLowerCase());
					// System.out.println(embeddingsMap.keySet().size());
					DenseVector wordVec = null;
					if (embedding != null) {

						wordVec = new DenseVector(embedding);
						// Adding all the word embeddings together
						sentenceEmbedding = (DenseVector) sentenceEmbedding
								.add(wordVec);
					} else {
						System.out.println("embedding null for ***"
								+ token.toLowerCase());
					}

				}
			}

			double scaleFactor = sentenceTokens.size();
			// taking the avaerage
			sentenceEmbedding = sentenceEmbedding.scale(1.0 / scaleFactor);
			System.out.println("Sentence embedding for" + sentenceCount + "\n"
					+ sentenceEmbedding);
			/*
			 * Serialize the sentence embedding
			 */
			VSMSentenceEmbeddingBean bean = new VSMSentenceEmbeddingBean();
			bean.setSentenceEmbedding(sentenceEmbedding);
			VSMSerializeSentenceEmbedding.serializeVectorBeanTrial(
					sentenceEmbedding, Integer.toString(sentenceCount));

		}

	}

}
