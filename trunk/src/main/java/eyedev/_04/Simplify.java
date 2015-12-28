package eyedev._04;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class Simplify extends ImageReader {
  private Simplifier simplifier;
  private ImageReader subDiscrim;

  public Simplify(Simplifier simplifier, ImageReader subDiscrim) {
    this.simplifier = simplifier;
    this.subDiscrim = subDiscrim;
  }

  public Simplify() {
  }

  public String readImage(BWImage image) {
    return subDiscrim.readImage(simplifier.simplify(image));
  }

  public Tree toTree() {
    Tree tree = OCRUtil.treeFor(this);
    tree.add(simplifier.toTree());
    tree.add(subDiscrim.toTree());
    return tree;
  }

  public void fromTree(Tree tree) {
    simplifier = (Simplifier) OCRUtil.fromTree(tree.get(0));
    subDiscrim = (ImageReader) OCRUtil.fromTree(tree.get(1));
  }
}
