package eyedev._08;

import eyedev._01.OCRImageUtil;
import eyedev._06.RecogUtil;
import prophecy.common.image.BWImage;

public class StandardFeatures {
  public static int getNumberOfFeatures() {
    return 4;
  }

  public static float extractFeature(BWImage image, int featureNr) {
    image = OCRImageUtil.trim(image); // work only on cropped images

    switch (featureNr) {
      case 0: return RecogUtil.upperWidthToLowerWidth(image);
      case 1: return RecogUtil.leftishness(image);
      case 2: return RecogUtil.uniqueWidths(image);
      case 3: return RecogUtil.uniqueLines(image);
    }
    return 0f;
  }

  public static String featureName(int featureNr) {
    switch (featureNr) {
      case 0: return "upper width to lower width";
      case 1: return "leftishness";
      case 2: return "unique widths";
      case 3: return "unique lines";
    }
    return "";
  }
}
