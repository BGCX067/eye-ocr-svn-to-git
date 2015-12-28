package eyedev._01;

import drjava.util.Tree;
import drjava.util.TreeUtil;
import eyedev._09.Segmenter;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class OCRUtil {
  private static final String masterPackage = "eyedev.";

  public static int discriminatorScore(ExampleSet exampleSet, ImageReader imageReader) {
    int score = 0;
    for (Example example : exampleSet.examples) {
      try {
        String answer = imageReader.readImage(example.image);
        if (example.text.equals(answer))
          ++score;
      } catch (Throwable t) {
        t.printStackTrace();
        return 0;
      }
    }
    return score;
  }

  public static boolean discriminatorWorks(ExampleSet exampleSet, ImageReader imageReader) {
    for (Example example : exampleSet.examples) {
      try {
        String answer = imageReader.readImage(example.image);
        if (!example.text.equals(answer))
          return false;
      } catch (Throwable t) {
        t.printStackTrace();
        return false;
      }
    }
    return true;
  }

  public static ImageReader makeImageReader(String description) {
    return (ImageReader) fromTree(Tree.parse(description));
  }

  public static String getImageReaderDescription(ImageReader imageReader) {
    return imageReader.toTree().toString();
  }

  public static ImageReaderStream makeImageReaderStream(List<String> imageReaders) {
    final Iterator<String> it = imageReaders.iterator();
    return new ImageReaderStream() {
      public String getNextImageReaderDescription() {
        return it.hasNext() ? it.next() : null;
      }
    };
  }

  public static Object fromTree(Tree tree) {
    if (tree.getName().startsWith("_")) {
      Tree tree2 = new Tree();
      tree2.set(tree);
      tree2.setName(masterPackage + tree.getName());
      tree = tree2;
    }
    return TreeUtil.treeToObject(tree);
  }

  public static Tree treeFor(Class aClass) {
    return new Tree(treeHeadForClass(aClass));
  }

  public static Tree treeFor(Describable describable) {
    return new Tree(treeHeadForClass(describable.getClass()));
  }

  public static String getImageReaderDescriptionWithLength(ImageReader imageReader) {
    String desc = getImageReaderDescription(imageReader);
    return "[length: " + desc.length() + "] " + desc;
  }

  public static ImageReader makeImageReader(Tree code) {
    return (ImageReader) fromTree(code);
  }

  public static Segmenter makeSegmenter(Tree code) {
    return (Segmenter) fromTree(code);
  }

  public static String treeHeadForClass(Class aClass) {
    String name = aClass.getName();
    if (name.startsWith(masterPackage + "_")) name = name.substring(masterPackage.length());
    return name;
  }

  public static Tree rectToTree(Rectangle r) {
    return r == null ? null
      : new Tree("rect").addInt(r.x).addInt(r.y).addInt(r.width).addInt(r.height);
  }

  public static Rectangle treeToRect(Tree tree) {
    return tree == null ? null
      : new Rectangle(tree.getInt(0), tree.getInt(1), tree.getInt(2), tree.getInt(3));
  }
}
