package VSMTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;

import no.uib.cipr.matrix.DenseVector;

import org.junit.Test;

import fig.basic.SysInfoUtils;
import VSMSerialization.VSMCountMap;
import VSMSerialization.VSMReadSerialCountMap;

public class TestSerializedCountMap {

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		File countMapObj = new File(
				"/group/project/vsm-nfs/PhraseEmbeddingsNPNew/phraseEmbeddings+folder_55.ser");
		ObjectInput in = new ObjectInputStream(new FileInputStream(countMapObj));
		HashMap<String, DenseVector> map = (HashMap<String, DenseVector>) in
				.readObject();
		System.out.println(map.values().iterator().next());
		System.out.println(map.keySet().size());

	}
}
