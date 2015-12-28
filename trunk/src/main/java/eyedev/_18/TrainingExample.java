package eyedev._18;

import drjava.util.Tree;
import eyedev._01.OCRUtil;

import java.awt.*;

public class TrainingExample {
  Tree tree;

  public TrainingExample() {
    tree = new Tree("TrainingExample");
  }

  public TrainingExample(Tree tree) {
    this.tree = tree;
  }

  public Tree getTree() {
    return tree;
  }

  public String getImageName() {
    return tree.getUnquotedString("image");
  }

  public void setImage(String imageName, int width, int height) {
    tree.setUnquotedString("image", imageName);
    tree.setInt("imageWidth", width);
    tree.setInt("imageHeight", height);
  }

  public Dimension getImageSize() {
    return new Dimension(tree.getInt("imageWidth", 0), tree.getInt("imageHeight", 0));
  }

  public void setText(String text) {
    tree.setUnquotedString("text", text);
  }

  public String getText() {
    return tree.getUnquotedString("text");
  }

  public void setOriginalImage(String originalImage) {
    tree.setUnquotedString("originalImage", originalImage);
  }

  public String getOriginalImage() {
    return tree.getUnquotedString("originalImage");
  }

  public void setOriginalClip(Rectangle originalClip) {
    tree.set("originalClip", OCRUtil.rectToTree(originalClip));
  }

  public Rectangle getOriginalClip() {
    return OCRUtil.treeToRect(tree.get("originalClip"));
  }
}
