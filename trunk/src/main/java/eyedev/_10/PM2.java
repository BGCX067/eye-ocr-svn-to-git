package eyedev._10;

import drjava.util.Tree;
import eyedev._01.*;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/* successor of _01.PhotographicMemory, storing images in a compressed form */
public class PM2 extends ImageReader implements CharacterLearner {
  public static class Item {
    public String codedImage, text;

    /** If unknown, set both to zero. But we want this to be known actually */
    public int topLine, baseLine;

    Item(String codedImage, String text, int topLine, int baseLine) {
      this.codedImage = codedImage;
      this.text = text;
      this.topLine = topLine;
      this.baseLine = baseLine;
    }

    public BWImage getImage() {
      return imageFromString(codedImage);
    }
  }

  List<Item> items = new ArrayList<Item>();

  public PM2() {}

  public PM2(ExampleSet exampleSet) {
    for (Example example : exampleSet.examples)
      addExample_noDuplicateCheck(example);
  }

  public String readImage(BWImage image) {
    for (Item item : items) {
      if (imageMatch(item.codedImage, image, 0f))
        return item.text;
    }
    return null;
  }

  public void addExample(Example example) {
    removeExample(example.image);
    addExample_noDuplicateCheck(example);
  }

  public void addExample_noDuplicateCheck(Example example) {
    items.add(new Item(imageToString(example.image), example.text, example.topLine, example.baseLine));
  }

  public String imageToString(BWImage image) {
    image = OCRImageUtil.trim(image);
    StringBuffer buf = new StringBuffer();
    int w = image.getWidth(), h = image.getHeight();
    buf.append(w).append(" ").append(h);

    boolean lastWhite = true;
    int runlength = 0;
    for (int y = 0; y < image.getHeight(); y++)
      for (int x = 0; x < image.getWidth(); x++) {
        boolean white = image.getPixel(x, y) == 1f;
        if (white != lastWhite) {
          buf.append(" ").append(runlength);
          runlength = 0;
          lastWhite = white;
        }
        ++runlength;
      }
    if (!lastWhite && runlength != 0)
      buf.append(" ").append(runlength);
    return buf.toString();
  }

  Iterator<Point> blackPixels(String imageDesc) {
    String[] l = imageDesc.split(" ");
    int w = Integer.parseInt(l[0]);
    List<Point> points = new ArrayList<Point>();
    int pos = 0;
    for (int i = 2; i < l.length; i += 2) {
      int n1 = Integer.parseInt(l[i]);
      int n2 = i+1 < l.length ? Integer.parseInt(l[i+1]) : 0;
      pos += n1;
      for (int j = 0; j < n2; j++) {
        points.add(new Point(pos % w, pos / w));
        ++pos;
      }
    }
    return points.iterator();
  }

  public static BWImage imageFromString(String imageDesc) {
    String[] l = imageDesc.split(" ");
    int w = Integer.parseInt(l[0]), h = Integer.parseInt(l[1]);
    BWImage image = new BWImage(w, h, 1f);
    int pos = 0;
    for (int i = 2; i < l.length; i += 2) {
      int n1 = Integer.parseInt(l[i]);
      int n2 = i+1 < l.length ? Integer.parseInt(l[i+1]) : 0;
      pos += n1;
      for (int j = 0; j < n2; j++) {
        image.setPixel(pos % w, pos / w, 0f);
        ++pos;
      }
    }
    return image;
  }

  boolean imageMatch(String s, BWImage image, float thresholdForMissingPixels) {
    image = new BWImage(image);

    Iterator<Point> it = blackPixels(s);
    int numThere = 0, numMissing = 0;
    while (it.hasNext()) {
      Point p = it.next();
      if (image.getPixel(p.x, p.y) == 1f) {
        if (thresholdForMissingPixels == 0f)
          return false;
        ++numMissing;
      } else {
        image.setPixel(p.x, p.y, 1f);
        ++numThere;
      }
    }

    float missing = (float) numMissing / (numMissing+numThere);
    if (missing >= thresholdForMissingPixels) return false;

    return OCRImageUtil.isAllWhite(image);
  }

  float erase(String s, BWImage image, int x1, int y1) {
    Iterator<Point> it = blackPixels(s);
    int numThere = 0, numMissing = 0;
    while (it.hasNext()) {
      Point p = it.next();
      p = new Point(x1+p.x, y1+p.y);
      if (p.x < 0 || p.y < 0 || p.x >= image.getWidth() || p.y >= image.getHeight()
        || image.getPixel(p.x, p.y) == 1f) {
        ++numMissing;
      } else {
        try {
          image.setPixel(p.x, p.y, 1f);
        } catch (RuntimeException e) {
          System.out.println(p + " image width: " + image.getWidth());
          throw e;
        }
        ++numThere;
      }
    }

    return (float) numMissing / (numMissing+numThere);
  }

  public Tree toTree() {
    Tree tExamples = new Tree();
    for (Item item : items) {
      Tree t;
      if (item.topLine != 0 || item.baseLine != 0) {
        t = new Tree().addUnquotedString(item.codedImage);
        t.setInt("top", item.topLine);
        t.setInt("base", item.baseLine);
      } else
        t = new Tree(item.codedImage);
      tExamples.add(t).addUnquotedString(item.text);
    }
    return OCRUtil.treeFor(this).add(tExamples);
  }

  public void fromTree(Tree tree) {
    Tree tExamples = tree.get(0);
    for (int i = 0; i < tExamples.namelessChildrenCount(); i += 2) {
      Tree t = tExamples.get(i);
      int topLine = 0, baseLine = 0;
      String image;
      if (t.namelessChildrenCount() != 0) {
        image = t.getUnquotedString(0);
        topLine = t.getInt("top");
        baseLine = t.getInt("base");
      } else {
        image = t.getName();
      }
      items.add(new Item(image, tExamples.getUnquotedString(i+1), topLine, baseLine));
    }
  }

  public float width(String image) {
    int idx = image.indexOf(" ");
    return Integer.parseInt(image.substring(0, idx));
  }

  public List<Item> getItems() {
    return items;
  }

  public void removeExample(BWImage image) {
    String codedImage = imageToString(image);
    for (PM2.Item item : items)
      if (item.codedImage.equals(codedImage)) {
        items.remove(item);
        return;
      }
  }

  public CharacterLearner getCharacterLearner() {
    return this;
  }

  public void learnCharacter(ImageWithMarkLines image, String text) {
    addExample(new Example(image, text));
  }

  public TreeSet<String> getKnownCharacters() {
    TreeSet<String> treeSet = new TreeSet<String>();
    for (Item item : items)
      treeSet.add(item.text);
    return treeSet;
  }

  public ExampleSet getExampleSet() {
    ExampleSet exampleSet = new ExampleSet();
    for (Item item : items) {
      ImageWithMarkLines imageWithMarkLines = new ImageWithMarkLines(imageFromString(item.codedImage), item.topLine, item.baseLine);
      exampleSet.add(new Example(imageWithMarkLines, item.text));
    }
    return exampleSet;
  }
}
