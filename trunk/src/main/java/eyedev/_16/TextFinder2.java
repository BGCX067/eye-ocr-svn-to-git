package eyedev._16;

import eyedev._01.OCRImageUtil;
import eyedev._12.Textfinder1;
import eyedev._12.TileType;
import prophecy.common.image.BWImage;

/** a text finder with a slightly different algorithm (more flexible - doesn't require white background) */
public class TextFinder2 extends Textfinder1 {
  public TextFinder2() {
  }

  @Override
  public TileType getTileType_mt(BWImage img) {
    //int pixels = img.getWidth()*img.getHeight();
    int numDark = OCRImageUtil.numPixelsDarkerThan(img, 0.1f);
    //int numBright = OCRImageUtil.numPixelsBrighterThan(img, 0.9f);
    //float dark = numDark / (float) pixels;
    //float bright = numBright / (float) pixels;

    if (numDark <= 4)
      return TileType.yellow; // no text
    else
      return TileType.blue; // possible text
  }
}
