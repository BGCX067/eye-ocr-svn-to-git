package eyedev._13;

import drjava.util.Pair;
import drjava.util.Tree;
import eyedev._01.*;
import eyedev._10.PM2;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CompareImages extends ExtendedImageReader {
  PM2 pm = new PM2();
  float threshold = 0.5f;

  //int standardWidth = 25, standardHeight = 25;
  int standardWidth = 40, standardHeight = 50;
  List<CacheItem> cache = null;
  double maxSizeRatioVariation = 0.25;
  private boolean removeDirt;
  private boolean findLigatures = false;

  static class CacheItem {
    BWImage image;
    String text;
    int originalWidth, originalHeight;
    PM2.Item originalItem;

    CacheItem(BWImage resizedImage, BWImage originalImage, PM2.Item item) {
      image = resizedImage;
      originalWidth = originalImage.getWidth();
      originalHeight = originalImage.getHeight();
      text = item.text;
      originalItem = item;
    }
  }

  public CompareImages() {
  }

  public CompareImages(ExampleSet exampleSet) {
    for (Example example : exampleSet.examples)
      addExample(example);
  }

  public RecognizedText extendedReadImage(InputImage inputImage) {
    BWImage image = inputImage.image;

    //System.out.println("items: " + pm.getItems().size());

    image = ImageProcessing.threshold(image, threshold);
    image = OCRImageUtil.trim(image);
    if (removeDirt) image = new DirtRemover().removeDirt(image);

    PM2.Item match = findExactMatch(image);
    if (match != null)
      return new RecognizedText(postprocess(inputImage, match.text));

    Pair<String, Float> pair = null;
    if (findLigatures)
      pair = findLigatureMatch(inputImage);

    pair = betterOf(pair, findApproximateMatch(image));

    if (pair != null) {
      //System.out.println("CompareImages confidence=" + pair.b);
      //System.out.println("CompareImages approx match: " + pair.a.text);
      return new RecognizedText(postprocess(inputImage, pair.a), pair.b);
    }

    return null;
  }

  private Pair<String, Float> betterOf(Pair<String, Float> pair1, Pair<String, Float> pair2) {
    if (pair1 == null) return pair2;
    if (pair2 == null) return pair1;
    return pair1.b >= pair2.b ? pair1 : pair2;
  }

  private String postprocess(InputImage inputImage, String text) {
    if (text == null) return null;
    return quickfixUpperLower(inputImage, text);
  }

  /** quick hack to distinguish similar-looking upper/lower characters in typical fonts
   *  (this should really be optional when we get truly professional) */
  // TODO: this breaks with the typewriter example (why)
  private String quickfixUpperLower(InputImage inputImage, String text) {
    if (text.length() != 1) return text;
    if (inputImage.baseLine == null || inputImage.topLine == null) return text;
    // character distance from top line relative to base-top
    double relTop = (0.0-inputImage.topLine)/(inputImage.baseLine-inputImage.topLine);
    //System.out.println("char: " + text + ", base: " + inputImage.baseLine + ", top: " + inputImage.topLine + ", relTop: " + relTop);
    String c = text.toLowerCase();
    boolean probablyLower = relTop > 0.25;
    if ("cwsopuvxz".indexOf(c) >= 0)
      return probablyLower ? c.toLowerCase() : c.toUpperCase();
    return text;
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

  private Pair<String, Float> findApproximateMatch(BWImage image) {
    makeCache();
    BWImage resizedImage = resize(image, standardWidth, standardHeight);
    Pair<String, Float> best = null;
    for (CacheItem item : cache) {
      if (!compatibleSizes(item.originalWidth, item.originalHeight, image.getWidth(), image.getHeight()))
        continue;
      float similarity = OCRImageUtil.similaritySameSize(item.image, resizedImage);
      if (best == null || best.b < similarity)
        best = new Pair<String, Float>(item.text, similarity);
    }
    return best;
  }

  // assume that image is a ligature (two characters touching each other)
  private Pair<String, Float> findLigatureMatch(InputImage inputImage) {
    makeCache();
    //BWImage resizedImage = resize(image, standardWidth, standardHeight);
    Pair<String, Float> best = null;

    // go through cache to find match for left character in image
    for (CacheItem item : cache) {
      // first, we need to find out where the splitting point is between the two characters (x coordinate)
      // for this, we need the learned item's width and adjust it to the input image
      // by looking at base line and top line
      int inputHeight = inputImage.baseLine-inputImage.topLine;
      int itemHeight = item.originalItem.baseLine-item.originalItem.topLine;
      int splitPoint = (int) (((float) item.originalWidth)*inputHeight/itemHeight);
      float relativeSplitPoint = ((float) splitPoint)/inputImage.image.getWidth();
      if (relativeSplitPoint < 0.2 || relativeSplitPoint > 0.8)
        continue;
      //System.out.println(item.text + " relativeSplitPoint=" + relativeSplitPoint);
      
      // split image at split point
      BWImage leftImage = inputImage.image.clip(0, 0, splitPoint, inputImage.image.getHeight());
      BWImage leftImageStandardSize = resize(leftImage, standardWidth, standardHeight);
      float similarity = OCRImageUtil.similaritySameSize(item.image, leftImageStandardSize);
      best = betterOf(best, new Pair<String, Float>(item.text + "*", similarity));

      /*if (!compatibleSizes(item.originalWidth, item.originalHeight, inputImage.getWidth(), inputImage.getHeight()))
        continue;
      float similarity = OCRImageUtil.similaritySameSize(item.image, resizedImage);
      if (best == null || best.b < similarity)
        best = new Pair<String, Float>(item, similarity);*/
    }
    return best;
  }


  private boolean compatibleSizes(int w1, int h1, int w2, int h2) {
    float ratio1 = ratio(w1, h1);
    float ratio2 = ratio(w2, h2);
    float ratio = ratio(ratio1, ratio2);
    return ratio >= 1 / (1 + maxSizeRatioVariation) && ratio <= 1 + maxSizeRatioVariation;
  }

  private synchronized void makeCache() {
    if (cache == null) {
      //System.out.println("CompareImages: Making cache (" + pm.getItems().size() + " entries)");
      cache = new ArrayList<CacheItem>();
      for (PM2.Item item : pm.getItems()) {
        BWImage image = item.getImage();
        BWImage resizedImage = resize(image, standardWidth, standardHeight);
        cache.add(new CacheItem(resizedImage, image, item));
      }
    }
  }

  private BWImage resize(BWImage image, int newSizeX, int newSizeY) {
    return ImageProcessing.resize(image, newSizeX, newSizeY);
  }

  private float ratio(float x, float y) {
    return x/y; // this is never called with 0 values or any other extreme shit, so simple division is ok
  }

  public void addExample(Example example) {
    pm.addExample(example);
    cache = null;
  }

  public void fromTree(Tree tree) {
    pm.fromTree(tree);
    removeDirt = tree.getBool("removeDirt", false);
    cache = null;
  }

  @Override
  public Tree toTree() {
    Tree tree = pm.toTree().setName(super.toTree().getName());
    if (removeDirt) tree.setBool("removeDirt", true);
    return tree;
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
        ImageWithMarkLines imageWithMarkLines = new ImageWithMarkLines(img, image.topLine, image.baseLine).trim();
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
