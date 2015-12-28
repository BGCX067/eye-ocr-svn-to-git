package eyedev._02;

import drjava.util.Tree;
import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class SingleBitDiscriminator extends ImageReader {
  int x, y;
  String blackText, whiteText;
  private boolean trained;

  public SingleBitDiscriminator() {}

  public SingleBitDiscriminator(ExampleSet exampleSet) {
    train(exampleSet);
  }

  public void train(ExampleSet exampleSet) {
    if (exampleSet.examples.size() != 2)
      return;

    Example a = exampleSet.examples.get(0);
    Example b = exampleSet.examples.get(1);

    for (int y = 0; y < a.image.getHeight(); y++)
      for (int x = 0; x < a.image.getWidth(); x++) {
        float p1 = a.image.getPixel(x, y);
        float p2 = b.image.getPixel(x, y);
        if (p1 != p2) {
          if (p1 < p2) {
            blackText = a.text;
            whiteText = b.text;
          } else {
            blackText = b.text;
            whiteText = a.text;
          }
          this.x = x;
          this.y = y;
          trained = true;
          return;
        }
      }
  }

  public String readImage(BWImage image) {
    if (!trained) return null;
    return image.getPixel(x, y) < 0.5f ? blackText : whiteText;
  }

  public Tree toTree() {
    Tree tree = OCRUtil.treeFor(this);
    if (trained) {
      tree.setInt("x", x).setInt("y", y);
      tree.setUnquotedString("b", blackText).setUnquotedString("w", whiteText);
    }
    return tree;
  }

  public void fromTree(Tree tree) {
    if (tree.get("x") != null) {
      trained = true;
      x = tree.getInt("x");
      y = tree.getInt("y");
      blackText = tree.getString("b");
      whiteText = tree.getString("w");
    }
  }
}
