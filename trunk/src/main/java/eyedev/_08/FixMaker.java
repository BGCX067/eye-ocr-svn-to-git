package eyedev._08;

import eyedev._01.ImageReader;
import prophecy.common.image.BWImage;

import java.util.List;

public class FixMaker {
  public static Fix makeFix(int featureNr, List<BWImage> images1, List<BWImage> images2, String char1, String char2, ImageReader recognizer) {
    Range range1 = getRange(featureNr, images1);
    Range range2 = getRange(featureNr, images2);

    if (range1.intersects(range2)) return null;

    // swap so that range1 < range2
    if (range1.min > range2.min) {
      Range range3 = range1;
      range1 = range2;
      range2 = range3;
      String char3 = char1;
      char1 = char2;
      char2 = char3;
    }

    String threshold = makeThreshold(range1, range2);

    Fix fix = new Fix(featureNr, threshold, char1, char2, recognizer);
    System.out.println("Fixing " + char1 + " vs " + char2
      + " (feature " + featureNr + ", " + range1 + "/" + range2 + ")");
    return fix;
  }

  /* find a value between the ranges and shorten the number to save chars */
  private static String makeThreshold(Range range1, Range range2) {
    float threshold = (range1.max+range2.min)/2;
    float lower = threshold * 0.9f + range1.max * 0.1f;
    float upper = threshold * 0.9f + range2.min * 0.1f;
    String s = String.valueOf(threshold);
    while (s.length() > 1 && s.indexOf("E") == -1 && s.charAt(s.length()-1) != '.') {
      String s2 = s.substring(0, s.length()-1);
      float newThreshold = Float.parseFloat(s2);
      if (newThreshold >= lower && newThreshold <= upper) {
        s = s2;
      } else
        break;
    }
    return s;
  }

  private static Range getRange(int featureNr, List<BWImage> images) {
    Range range = new Range();
    for (BWImage image : images)
      range.add(StandardFeatures.extractFeature(image, featureNr));
    return range;
  }

}
