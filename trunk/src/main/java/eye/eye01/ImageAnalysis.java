package eye.eye01;

import eyedev._05.SegmentCounter;
import eyedev._08.StandardFeatures;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

public class ImageAnalysis {
  static String getAnalysis(RGBImage rgbImage) {
    StringBuffer text = new StringBuffer("Image properties:\n\n");
    BWImage image = rgbImage.toBW();
    for (int i = 0; i < StandardFeatures.getNumberOfFeatures(); i++) {
      text.append(StandardFeatures.featureName(i) + ": " + StandardFeatures.extractFeature(image, i) + "\n");
    }
    text.append("left-to-right segment signature: " + SegmentCounter.getSegmentCount(image, true) + "\n");
    text.append("top-to-bottom segment signature: " + SegmentCounter.getSegmentCount(image, false) + "\n");
    text.append("extended t-t-b segment signature: " + SegmentCounter.getExtendedTTBSignature(image) + "\n");
    return text.toString();
  }
}
