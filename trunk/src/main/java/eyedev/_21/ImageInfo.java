package eyedev._21;

import drjava.util.Tree;

import java.io.File;

public class ImageInfo {
  private Tree tree;
  private File file;

  public ImageInfo(File file, Tree tree) {
    this.file = file;
    this.tree = tree;
  }

  public ImageInfo() {
    tree = new Tree();
  }

  public Tree getTree() {
    return tree;
  }

  public String getImagePath() {
    return tree.getUnquotedString("image");
  }

  public void setImagePath(String imagePath) {
    tree.setUnquotedString("image", imagePath);
  }

  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public Corrections getCorrections() {
    return new Corrections(tree.subTree("corrections"));
  }
}
