package VSMPhraseEmbeddings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import VSMLogger.VSMLogger;
import VSMUtilityClasses.VSMUtil;
import no.uib.cipr.matrix.DenseVector;

public class CosineSimilariyApp {

	private static final Logger LOGGER;
	private static final String embeddingsMapPath;
	private static HashMap<String, DenseVector> embeddingsMap;
	private static String userInPhrase;
	private static HashMap<String, Double> similarityMap;

	static {
		LOGGER = VSMLogger.setup(CosineSimilariyApp.class.getName());
		embeddingsMapPath = "/group/project/vsm-nfs/phraseEmbeddings_new/embeddings.ser";
		similarityMap = new LinkedHashMap<String, Double>();
	}

	public static void main(String... args) {
		deserializeEmbeddingsMap();
		getUserInput(args);
		formCosineSimilarityMap();
		freeUpMemory();
		sortSimilarityMap();
		display5();
	}

	private static void display5() {
		System.out.println("DISPLAYING THE TOP 5 SIMILAR PHRASES");
		ArrayList<String> keys = new ArrayList<String>(similarityMap.keySet());
		int itr = 0;
		for (int i = keys.size() - 1; i >= 0; i--) {
			System.out.println(keys.get(i) + "\t"
					+ similarityMap.get(keys.get(i)));
			itr++;
			if (itr == 20) {
				break;
			}
		}

	}

	private static void sortSimilarityMap() {
		System.out.println("SORTING THE SIMILARITY MAP");
		similarityMap = (LinkedHashMap<String, Double>) sortByValue(similarityMap);

	}

	private static void freeUpMemory() {
		embeddingsMap = null;

	}

	private static void formCosineSimilarityMap() {

		System.out.println("FORMING COSINE SIMILARITY MAP");
		if (embeddingsMap.keySet().contains(userInPhrase)) {
			DenseVector userInPhraseVec = embeddingsMap.get(userInPhrase);
			System.out.println(userInPhraseVec);
			formMap(userInPhraseVec);
		} else {
			System.out.println("the phrase is not contained");
		}

		System.out.println("DONE: " + similarityMap.keySet().size());
	}

	private static void formMap(DenseVector userInPhraseVec) {
		for (String phrase : embeddingsMap.keySet()) {
			DenseVector vec = embeddingsMap.get(phrase);
			double cosineSim = VSMUtil.cosineSimilarity(vec.getData(),
					userInPhraseVec.getData());
			if (!Double.isNaN(cosineSim))
				similarityMap.put(phrase, cosineSim);
		}
	}

	private static void getUserInput(String[] args) {
		if (args.length > 0) {
			userInPhrase = args[0];
		} else {
			System.out.println("PLEASE ENTER A PHRASE TO TEST");
			System.exit(-1);
		}

		System.out.println("USER ENTERED: " + userInPhrase);

	}

	private static void deserializeEmbeddingsMap() {
		System.out.println("DESERIALIZING SIMILARITY MAP");
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(embeddingsMapPath));
			embeddingsMap = (HashMap<String, DenseVector>) in.readObject();
			System.out.println(embeddingsMap.keySet().size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("DONE++ " + embeddingsMap.keySet().size());

	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
