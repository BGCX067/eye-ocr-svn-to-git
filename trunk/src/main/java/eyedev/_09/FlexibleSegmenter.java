package eyedev._09;

import drjava.util.Tree;
import eyedev._01.DebugItem;
import eyedev._01.OCRImageUtil;
import eyedev._17.BaseLineFinder;
import eyedev._17.MarkLine;
import eyedev._17.TopLineFinder;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FlexibleSegmenter extends Segmenter {
  private BWImage baseImage;
  private float threshold = 0.5f;

  public FlexibleSegmenter() {
  }

  public List<Segment> segment(BWImage img) {
    // trim image (remove white space) and then add 1 pixel of white space on all sides

    baseImage = ImageProcessing.threshold(img, threshold);
    Rectangle box = OCRImageUtil.getBoundingBox(baseImage);
    Point translation = new Point(box.x - 1, box.y - 1);
    baseImage = ImageProcessing.addBorder(OCRImageUtil.trim(baseImage), 1);

    findMarkLines(baseImage, translation);

    List<Segment> letters = new ArrayList<Segment>();
    int w = baseImage.getWidth(), h = baseImage.getHeight();
    int lastSep = 0;
    int[] lastTrail = null;
    xloop: for (int x1 = 0; x1 < w; x1++) {
      int x = x1;
      int[] trail = new int[h];
      for (int y = 0; y < h-1; y++) {
        //markedImage.setPixel(x, y, new RGB(Color.red));
        trail[y] = x;

        // The parts right of the && are so we don't step diagonally over 1-pixel wide lines
        boolean l = baseImage.getPixel(x-1, y+1) >= threshold && baseImage.getPixel(x-1, y) >= threshold;
        boolean m = baseImage.getPixel(x, y+1) >= threshold;
        boolean r = baseImage.getPixel(x+1, y+1) >= threshold && baseImage.getPixel(x+1, y) >= threshold;
        
        if (m) {
          // ok, proceed downwards
        } else if (l) {
          --x;
        } else if (r) {
          ++x;
        } else
          continue xloop;
      }
      trail[h-1] = x;
      if (x > lastSep+1) {
        //System.out.println("Cutting out letter " + (letters.size()+1));
        Segment letter = cutOutLetter(lastTrail, trail);
        letter.boundingBox.translate(translation.x, translation.y);
        //System.out.println("Letter size: " + letter.segmentImage.getWidth() + "*" + letter.segmentImage.getHeight());
        if (letter.segmentImage.getWidth() != 0 && letter.segmentImage.getHeight() != 0)
          letters.add(letter);
        lastTrail = trail;
      }
      //System.out.println("x1=" + x1 + ", x=" + x + ", w=" + w);
      lastSep = x;
    }
    //System.out.println("letters: " + letters.size());
    return letters;
  }

  private void findMarkLines(BWImage image, Point translation) {
    MarkLine baseLine = new BaseLineFinder().findBaseLine(image);
    if (baseLine != null)
      addDebugItem(new DebugItem("Base line", baseLine.translate(translation.x, translation.y)));
    MarkLine topLine = new TopLineFinder().findTopLine(image);
    if (topLine != null)
      addDebugItem(new DebugItem("Top line", topLine.translate(translation.x, translation.y)));
  }

  private Segment cutOutLetter(int[] trail1, int[] trail2) {
    // TODO (optimization): make a smaller image to begin with
    BWImage letter = new BWImage(baseImage.getWidth(), baseImage.getHeight(), 1f);
    for (int y = 0; y < baseImage.getHeight(); y++) {
      int x1 = trail1 == null ? 0 : trail1[y];
      int x2 = trail2[y];
      ImageProcessing.copy(baseImage, x1, y, letter, x1, y, x2-x1, 1);
    }
    //OCRImageUtil.show(letter.toRGB());
    return new Segment(SegmentLevel.character, OCRImageUtil.getBoundingBox(letter), OCRImageUtil.trim(letter));
  }

  @Override
  public void fromTree(Tree tree) {
    threshold = tree.getFloat("threshold", threshold);
  }

  @Override
  public Tree toTree() {
    return super.toTree().setFloat("threshold", threshold);
  }

  public float getThreshold() {
    return threshold;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }
}
