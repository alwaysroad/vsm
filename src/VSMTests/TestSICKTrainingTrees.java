package VSMTests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class TestSICKTrainingTrees {

	public static void main(String[] args) {
		String sickTrainingSet = "/disk/gpfs/scohen/embeddings/datasets/dsm/SICK_train_trees.txt";
		BufferedReader br = null;
		int count = 0;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(sickTrainingSet));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(count);
				count++;
			}

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("count**" + count);
		}
	}
}
