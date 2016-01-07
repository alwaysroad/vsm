package VSMTests;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public class TestOrdering {

	public static void main(String... args) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		MultiValueMap map = new MultiValueMap();
		File[] files = new File(
				"/group/corpora/public/bllip/bllip_nanc_mem/data/000")
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						// TODO Auto-generated method stub
						return !pathname.isHidden();
					}
				});
		StringTokenizer tokenizer = null;
		for (File file : files) {
			String[] tokens = file.getName().split("-");
			for (int i = 0; i < tokens.length - 1; i++) {
				map.put(tokens[0], tokens[i + 1]);
			}
		}

		for (Object obj : map.keySet()) {
			// System.out.println(obj + "	" + map.get(obj));
		}
		List<String> list1 = new ArrayList<String>(map.keySet());
		Collections.sort(list1, new Comparator<String>() {
			public int compare(String string1, String string2) {
				String a = string1.replaceFirst("^0+(?!$)", "");
				String b = string2.replaceFirst("^0+(?!$)", "");
				return Integer.parseInt(a) - Integer.parseInt(b);
			}
		});

		for (Object i : list1) {
			String s2 = i.toString();
			for (Object s : map.getCollection(i)) {
				String s1 = s.toString();
				s2 = s2 + "-" + s1;
			}

			System.out.println(s2);
			System.out
					.println(new File(
							"/group/corpora/public/bllip/bllip_nanc_mem/data/000/"
									+ s2).exists());
		}
	}
}
