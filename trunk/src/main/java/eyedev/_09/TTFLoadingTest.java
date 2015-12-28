package eyedev._09;

import eye.eye01.TextPainter2;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TTFLoadingTest {
  public static void main(String[] args) throws IOException, FontFormatException {
    File fontFile = new File("fonts/falcon.ttf");
    Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
    font = font.deriveFont(30f);
    BWImage image = new TextPainter2(font).makeImage("TTF LOADED from FILE!");
    SurfaceUtil.showAsMain(fontFile.getPath(), new ImageSurface(image));
  }
}
