package eye.eye03;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;

public class RecognizerInfo implements Cloneable {
  Tree tree;

  public RecognizerInfo(Tree tree) {
    this.tree = tree;
  }

  public RecognizerInfo(Tree code, String name) {
    tree = new Tree("recognizer");
    tree.addUnquotedString(name);
    tree.add(code);
  }

  public Tree getCode() {
    return tree.get(1);
  }

  public void setCode(Tree code) {
    tree.set(1, code);
  }

  public String getName() {
    return tree.getUnquotedString(0);
  }

  public String getPreferredFontName() {
    return tree.getUnquotedString("preferredFont");
  }

  public void setPreferredFontName(String preferredFontName) {
    tree.setUnquotedString("preferredFont", preferredFontName);
  }

  public Tree getTree() {
    return tree;
  }

  public void setName(String name) {
    tree.setUnquotedString(0, name);
  }

  public ImageReader getImageReader() {
    return OCRUtil.makeImageReader(getCode());
  }

  public RecognizerInputType getInputType() {
    int i = tree.getInt("inputType", 0);
    if (i == 1) return RecognizerInputType.character;
    if (i == 2) return RecognizerInputType.line;
    if (i == 3) return RecognizerInputType.lines;
    return RecognizerInputType.unknown;
  }

  public void setInputType(RecognizerInputType type) {
    int i = 0;
    if (type == RecognizerInputType.character) i = 1;
    if (type == RecognizerInputType.line) i = 2;
    if (type == RecognizerInputType.lines) i = 3;
    tree.setInt("inputType", i);
  }

  public RecognizerInfo clone() {
    try {
      return (RecognizerInfo) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public String getComment() {
    return tree.getUnquotedString("comment");
  }

  public void setComment(String comment) {
    tree.setUnquotedString("comment", comment);
  }
}
