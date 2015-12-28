package eyedev._16;

import eyedev._01.DebugItem;
import eyedev._01.OCRImageUtil;
import eyedev._01.Option;
import eyedev._09.Segment;
import eyedev._09.SegmentLevel;
import eyedev._09.Segmenter;
import eyedev._17.MarkLine;
import eyedev._17.BaseLineFinder;
import eyedev._17.TopLineFinder;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** a character segmenter for fixed-width text (typewriter fonts) */
public class PoppySegmenter extends Segmenter {
  public float maxWidthVariation = 0.2f;
  public float blackWhiteThreshold = 0.1f;
  public float whitenessTolerance = 0.0f;

  public float defaultCharacterWidth = 25.0f;

  /** returns a list of split points (x values, n-1 elements for n characters) */
  public List<Integer> segmentCharacters(BWImage image) {
    findBaseLine(image);

    List<Integer> result = new ArrayList<Integer>();
    int w = image.getWidth(), h = image.getHeight();
    double[] whitenessMap = new double[w];
    for (int x = 0; x < w; x++)
      whitenessMap[x] = columnWhiteness(image, h, x);
    int x = 0;
    result.add(x);
    while (x < w) {
      int x1 = x + (int) (defaultCharacterWidth*(1-maxWidthVariation));
      int x2 = x + (int) (defaultCharacterWidth*(1+maxWidthVariation))+1;
      int preferredX = x + (int) defaultCharacterWidth;
      if (x1 > w) {
        // reached right border
        // add rest of line as another character if we have at least half the standard char width
        if (w-x >= defaultCharacterWidth*0.5)
          result.add(w);
        break;
      }
      if (x2 > w) x2 = w;
      if (x2 <= x1) {
        result.add(x2);
        break;
      }
      x = findBestSplitPoint(whitenessMap, x1, x2, preferredX);
      result.add(x);
    }
    return result;
  }

  private void findBaseLine(BWImage image) {
    MarkLine baseLine = new BaseLineFinder().findBaseLine(image);
    if (baseLine != null)
      addDebugItem(new DebugItem("Base line", baseLine));
    MarkLine topLine = new TopLineFinder().findTopLine(image);
    if (topLine != null)
      addDebugItem(new DebugItem("Top line", topLine));
  }

  private double columnWhiteness(BWImage image, int h, int x) {
    int count = OCRImageUtil.numPixelsBrighterThan(image.clip(new Rectangle(x, 0, 1, h)), blackWhiteThreshold);
    return count/(double) h;
  }

  private int findBestSplitPoint(double[] whitenessMap, int x1, int x2, int preferredX) {
    double maxWhiteness = 0;
    for (int x = x1; x < x2; x++)
      maxWhiteness = Math.max(maxWhiteness, whitenessMap[x]);
    double threshold = maxWhiteness-whitenessTolerance;
    boolean[] candidate = new boolean[x2-x1];
    for (int x = x1; x < x2; x++) candidate[x-x1] = whitenessMap[x] >= threshold;

    int xStart = -1;
    findStart: for (int i = 0; i < Math.max(preferredX-x1, x2-preferredX); i++)
      for (int lr = 0; lr < 2; lr++) {
        int x = lr == 0 ? preferredX+i : preferredX-i;
        if (x < x1 || x >= x2) continue;
        if (candidate[x-x1]) {
          xStart = x;
          break findStart;
        }
      }
    if (xStart == -1) return preferredX;

    int xleft = xStart, xright = xStart;
    while (xleft > x1 && candidate[xleft-1-x1]) --xleft;
    while (xright < x2-1 && candidate[xright+1-x1]) ++xright;
    //System.out.println("xleft=" + xleft + ", xright=" + xright);
    return (xright+xleft)/2;
  }

  @Override
  public List<Segment> segment(BWImage baseImage) {
    List<Integer> splitPoints = segmentCharacters(baseImage);
    List<Segment> segments = new ArrayList<Segment>();
    for (int i = 1; i < splitPoints.size(); i++) {
      int x1 = splitPoints.get(i-1);
      int x2 = splitPoints.get(i);
      Rectangle boundingBox = new Rectangle(x1, 0, x2 - x1, baseImage.getHeight());
      segments.add(new Segment(SegmentLevel.character, boundingBox, baseImage.clip(boundingBox)));
    }
    return segments;
  }

  @Override
  public void collectOptions(List<Option> options) {
    options.add(new Option(this, "Character width (pixels)",
      Option.Type.floatOption, String.valueOf(defaultCharacterWidth)));
  }

  @Override
  public void changeOption(Option option) {
    if (option.name.equals("Character width (pixels)")) {
      float value = option.floatValue();
      if (!Float.isNaN(value))
        defaultCharacterWidth = value;
    }
  }
}
