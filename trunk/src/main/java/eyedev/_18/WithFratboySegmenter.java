package eyedev._18;

import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._09.FlexibleSegmenter;
import eyedev._09.SaR;
import eyedev._09.Segmenter;
import eyedev._13.IRSegmenter;
import eyedev._14.EliminateLargeLetters;
import eyedev._14.LineFinder;
import eyedev._20.DelegatingImageReader;
import prophecy.common.image.BWImage;

/** current standard lines/line segmenter */
public class WithFratboySegmenter extends DelegatingImageReader implements IRSegmenter {
  private ImageReader characterRecognizer;
  private BWImage image;

  public WithFratboySegmenter() {
  }

  public WithFratboySegmenter(ImageReader characterRecognizer) {
    this.characterRecognizer = characterRecognizer;
  }

  public String readImage(BWImage image) {
    this.image = image;
    return super.readImage(image);
  }

  public void setCharRecognizer(String description) {
    characterRecognizer = OCRUtil.makeImageReader(description);
  }

  public String rerecognize() {
    return readImage(image);
  }

  public ImageReader makeImageReader() {
    Segmenter lineFinder = new EliminateLargeLetters(new LineFinder());
    FlexibleSegmenter lineSegmenter = new FlexibleSegmenter();
    lineSegmenter.setThreshold(0.75f);
    return new SaR(lineFinder, lineSegmenter, characterRecognizer);
  }
}
