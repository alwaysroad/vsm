package VSMUtilityClasses;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.nlp.PCFGLA.TreeAnnotations;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;

public class PTBTreeDenormaliser extends PTBTreeProcessor {
	private boolean isDenormalize;

	public PTBTreeDenormaliser(boolean isDenormalize) {
		this.isDenormalize = isDenormalize;
	}

	@Override
	public Tree<String> process(Tree<String> tree) {
		Tree<String> denormalizedTree;

		if (isDenormalize) {
			// System.err.println(tree.toString());

			denormalizedTree = restoreUnaryRules(tree);

			// System.err.println(denormalizedTree.toString());

			denormalizedTree = TreeAnnotations.unAnnotateTree(denormalizedTree);

			// System.err.println(denormalizedTree.toString());

			// System.exit(0);

		} else {
			denormalizedTree = tree;
		}
		return denormalizedTree;
	}

	private static Tree<String> restoreUnaryRules(Tree<String> rootTree) {

		if (!rootTree.isLeaf()) {
			// Process children first
			List<Tree<String>> newChildren = new ArrayList<Tree<String>>();

			List<Tree<String>> oldChildren = rootTree.getChildren();
			for (int i = 0; i < oldChildren.size(); i++) {
				Tree<String> newChild = restoreUnaryRules(oldChildren.get(i));
				newChildren.add(newChild);
			}

			String rootLabel = rootTree.getLabel();
			Tree<String> newRootTree = expandUnaryLabel(rootLabel, newChildren);
			return newRootTree;
		} else {
			return rootTree;
		}
	}

	private static Tree<String> expandUnaryLabel(String rootLabel,
			List<Tree<String>> children) {
		if (rootLabel.contains("|")) {
			int firstIndex = rootLabel.indexOf("|");
			String toplabel = rootLabel.substring(0, firstIndex);
			Tree<String> newRootTree = new Tree<String>(toplabel);

			String restlabel = rootLabel.substring(firstIndex + 1);
			Tree<String> newChild = expandUnaryLabel(restlabel, children);

			List<Tree<String>> newChildren = new ArrayList<Tree<String>>();
			newChildren.add(newChild);
			newRootTree.setChildren(newChildren);
			return newRootTree;
		} else {
			Tree<String> newRootTree = new Tree<String>(rootLabel);
			newRootTree.setChildren(children);
			return newRootTree;
		}
	}

}
