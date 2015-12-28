package eyedev._20;

import eyedev._01.ImageReader;
import eyedev._09.FlexibleSegmenter;
import eyedev._09.SaR;
import eyedev._09.Segmenter;
import eyedev._13.CompareImages;

public class ScreenshotRecognizer extends DelegatingImageReader {
  public ImageReader makeImageReader() {
    ScreenshotTextFinder textFinder = new ScreenshotTextFinder();
    Segmenter lineSegmenter = new FlexibleSegmenter();
    //ImageReader charRecognizer = EyeStandardCharacterRecognizers.getAlpha7().getImageReader();
    ImageReader charRecognizer = new CompareImages();
    SaR sar = new SaR(textFinder, lineSegmenter, charRecognizer);
    sar.setSpaceThreshold(0.3f);
    return sar;
  }
}
