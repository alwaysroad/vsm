package VSMUtilityClasses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class FileLineIterator implements Iterator<Tree<String>>, Iterable<Tree<String>> {
	private String filePath;
	private int count;
	
	private PennTreeReader treeReader;
	private PTBTreeProcessor ptbTreeprocessor;
	
	// PTBTreeNormaliser ptbTreeNormaliser;
	//private UnknownWordsPOSReplacer unknownWordPOSReplacer = null;

	public FileLineIterator(String filePath, PTBTreeProcessor ptbTreeNormaliser){
		this.filePath = filePath;
		this.ptbTreeprocessor = ptbTreeNormaliser;
		
		InputStreamReader inputData;
		try {
			inputData = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
			treeReader = new PennTreeReader(inputData);
			this.count = 0;
		} catch (IOException e) {
			System.err.println("Error with the input/output: "+e.getMessage());
		}
	}
	
	public int getCurrentCount() {
		return count;
	}
	
	//	public void setReplacer(UnknownWordsPOSReplacer unknownWordPOSReplacer) {
	//		System.err.println("Setting replacer to " + unknownWordPOSReplacer);
	//		this.unknownWordPOSReplacer = unknownWordPOSReplacer;
	//	}
	
	@Override
	public boolean hasNext() {
		return treeReader.hasNext();
	}

	@Override
	public Tree<String> next() {
		if (treeReader.hasNext()) {
			count += 1;
			Tree<String> tree = treeReader.next();
			Tree<String> normalisedTree = ptbTreeprocessor.process(tree);
			//			if (unknownWordPOSReplacer != null){
			//				unknownWordPOSReplacer.fixTreeUnknownWords(normalisedTree);
			//			}
			return normalisedTree;
		}else{
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("FileLineIterator.remove() is not supported");		
	}

	@Override
	public Iterator<Tree<String>> iterator() {
		FileLineIterator t = new FileLineIterator(filePath, ptbTreeprocessor);
		//t.setReplacer(unknownWordPOSReplacer);
		return t;
	}
}
