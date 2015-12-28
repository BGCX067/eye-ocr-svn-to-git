package eyedev._16;

import drjava.util.Tree;
import eye.eye04.EyeStandardCharacterRecognizers;
import eyedev._01.CharacterLearner;
import eyedev._01.ImageReader;
import eyedev._09.SaR;
import prophecy.common.image.BWImage;

public class PoppyRecognizer extends ImageReader {
  private SaR sar;

  public String readImage(BWImage image) {
    makeSaR();
    debug("PoppyRecognizer: delegating");
    return delegateRecognition(sar, image);
  }

  private SaR makeSaR() {
    if (sar == null) {
      debug("PoppyRecognizer: Making objects");
      PoppyLineFinder lineFinder = new PoppyLineFinder();
      PoppySegmenter lineSegmenter = new PoppySegmenter();
      ImageReader charRecognizer = EyeStandardCharacterRecognizers.getAlpha7().getImageReader();
      sar = new SaR(lineFinder, lineSegmenter, charRecognizer);
    }
    return sar;
  }

  public CharacterLearner getCharacterLearner() {
    return makeSaR().getCharacterLearner();
  }

  public Tree toTree() {
    return makeSaR().toTree();
  }
}
