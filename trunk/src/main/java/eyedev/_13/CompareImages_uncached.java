package eyedev._13;

import drjava.util.Pair;
import drjava.util.Tree;
import eyedev._01.*;
import eyedev._10.PM2;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;
import java.util.TreeSet;

public class CompareImages_uncached extends ExtendedImageReader {
  PM2 pm = new PM2();
  float threshold = 0.5f;

  public RecognizedText extendedReadImage(InputImage inputImage) {
    BWImage image = inputImage.image;
    //System.out.println("items: " + pm.getItems().size());

    image = ImageProcessing.threshold(image, threshold);
    image = OCRImageUtil.trim(image);

    PM2.Item match = findExactMatch(image);
    if (match != null)
      return new RecognizedText(match.text);

    Pair<PM2.Item, Float> pair = findApproximateMatch(image);
    if (pair != null) {
      //System.out.println("CompareImages confidence=" + pair.b);
      return new RecognizedText(pair.a.text, pair.b);
    }

    return null;
  }

  private PM2.Item findExactMatch(BWImage image) {
    String codedImage = pm.imageToString(image);
    //System.out.println("findExactMatch: " + codedImage);
    for (PM2.Item item : pm.getItems()) {
      if (item.codedImage.equals(codedImage))
        return item;
    }
    return null;
  }

  private Pair<PM2.Item, Float> findApproximateMatch(BWImage image) {
    Pair<PM2.Item, Float> best = null;

    for (PM2.Item item : pm.getItems()) {
      float similarity = similarity(item.getImage(), image);
      if (best == null || best.b < similarity)
        best = new Pair<PM2.Item, Float>(item, similarity);
    }
    return best;
  }

  private float similarity(BWImage image1, BWImage image2) {
    if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
      float ratio1 = ratio(image1.getWidth(), image1.getHeight());
      float ratio2 = ratio(image2.getWidth(), image2.getHeight());
      float ratio = ratio(ratio1, ratio2);
      if (ratio <= 0.5f || ratio >=  2.0f)
        return 0f; // don't bother with extreme resizings

      int newSizeX = Math.max(image1.getWidth(), image2.getWidth());
      int newSizeY = Math.max(image1.getHeight(), image2.getHeight());

      image1 = resize(image1, newSizeX, newSizeY);
      image2 = resize(image2, newSizeX, newSizeY);
    }

    return OCRImageUtil.similaritySameSize(image1, image2);
  }

  private BWImage resize(BWImage image, int newSizeX, int newSizeY) {
    if (newSizeX != image.getWidth() || newSizeY != image.getHeight()) {
      /*System.out.println("Resizing image: " + image.getWidth() + "*" + image.getHeight()
        + " -> " + newSizeX + "*" + newSizeY);*/
      return ImageProcessing.resize(image, newSizeX, newSizeY);
    }
    return image;
  }

  private float ratio(float x, float y) {
    return x/y; // this is never called with 0 values or any other extreme shit, so simple division is ok
  }

  public void addExample(BWImage image, String text) {
    pm.addExample(new Example(image, text));
  }

  public void fromTree(Tree tree) {
    pm.fromTree(tree);
  }

  @Override
  public Tree toTree() {
    return pm.toTree().setName(super.toTree().getName());
  }

  public int numExamples() {
    return pm.getItems().size();
  }

  public PM2 getPM2() {
    return pm;
  }

  public CharacterLearner getCharacterLearner() {
    return new CharacterLearner() {
      public void learnCharacter(ImageWithMarkLines image, String text) {
        BWImage img = ImageProcessing.threshold(image.image, threshold);
        Rectangle r = OCRImageUtil.getBoundingBox(img);
        ImageWithMarkLines imageWithMarkLines = new ImageWithMarkLines(img.clip(r));
        if (image.topLine != 0 || image.baseLine != 0) {
          imageWithMarkLines.topLine = image.topLine-r.y;
          imageWithMarkLines.baseLine = image.baseLine-r.y;
        }
        pm.learnCharacter(imageWithMarkLines, text);
      }

      public TreeSet<String> getKnownCharacters() {
        return pm.getKnownCharacters();
      }

      public ExampleSet getExampleSet() {
        return pm.getExampleSet();
      }
    };
  }

  public boolean isParallelizable() {
    return true;
  }
}
