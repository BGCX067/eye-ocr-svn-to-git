package eyedev._21;

import drjava.util.Tree;
import eyedev._01.OCRUtil;

import java.awt.*;

public class Correction {
  private Tree tree;

  public Correction(Tree tree) {
    this.tree = tree;
  }

  public Correction(Rectangle r, String text) {
    tree = new Tree();
    tree.set("r", OCRUtil.rectToTree(r));
    tree.setUnquotedString("text", text);
  }

  public Tree toTree() {
    return tree;
  }

  public Rectangle getRectangle() {
    return OCRUtil.treeToRect(tree.get("r"));
  }

  public String getText() {
    return tree.getUnquotedString("text");
  }
}
