package eyedev._01;

import drjava.util.Tree;
import prophecy.common.image.BWImage;

import java.util.ArrayList;
import java.util.List;

public class PhotographicMemory extends ImageReader {
  static class Item {
    String image, text;

    Item(String image, String text) {
      this.image = image;
      this.text = text;
    }
  }

  private List<Item> items = new ArrayList<Item>();

  public PhotographicMemory() {}
  
  public PhotographicMemory(ExampleSet exampleSet) {
    train(exampleSet);
  }

  public String readImage(BWImage image) {
    for (Item item : items) {
      if (imageMatch(item.image, image))
        return item.text;
    }
    return null;
  }

  public void train(ExampleSet exampleSet) {
    for (Example example : exampleSet.examples) {
      items.add(new Item(imageToString(example.image), example.text));
    }
  }

  String imageToString(BWImage image) {
    char[] c = new char[image.getWidth()*image.getHeight()];
    for (int y = 0; y < image.getHeight(); y++)
      for (int x = 0; x < image.getWidth(); x++) {
        int i = y*image.getWidth()+x;
        char ch = image.getPixel(x, y) == 1.0f ? ' ' : 'X';
        c[i] = ch;
      }
    return new String(c);
  }

  private boolean imageMatch(String s, BWImage image) {
    if (s.length() != image.getWidth()*image.getHeight())
      return false;
    
    for (int y = 0; y < image.getHeight(); y++)
      for (int x = 0; x < image.getWidth(); x++) {
        int i = y*image.getWidth()+x;
        float color = s.charAt(i) == ' ' ? 1.0f : 0.0f;
        if (color != image.getPixel(x, y))
          return false;
      }
    return true;
  }

  public Tree toTree() {
    Tree tExamples = new Tree();
    for (Item item : items) {
      tExamples.addUnquotedString(item.image).addUnquotedString(item.text);
    }
    return OCRUtil.treeFor(this).add(tExamples);
  }

  public void fromTree(Tree tree) {
    Tree tExamples = tree.get(0);
    for (int i = 0; i < tExamples.namelessChildrenCount(); i += 2) {
      items.add(new Item(tExamples.getUnquotedString(i), tExamples.getUnquotedString(i+1)));
    }
  }
}
