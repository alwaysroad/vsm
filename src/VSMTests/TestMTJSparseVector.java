package VSMTests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.uib.cipr.matrix.sparse.SparseVector;

import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Before;
import org.junit.Test;

import VSMUtilityClasses.BLLIPCorpusReader;

public class TestMTJSparseVector {

	@Test
	public void testMTJSparseVector() {
		SparseVector sv = new SparseVector(4);
		/*
		 * Testing the sparse vector
		 */
		sv.add(0, 1.0);
		sv.add(1, 1.0);
		sv.add(2, 1.0);
		sv.add(3, 1.0);
		
		System.out.println(sv);
		
		/*
		 * Testing the two methods that are needed
		 */
		System.out.println(sv.getIndex());
		System.out.println(sv.getData());
	}
}
