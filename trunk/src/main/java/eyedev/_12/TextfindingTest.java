package eyedev._12;

import eyedev._01.OCRImageUtil;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TextfindingTest {
  public static void main(String[] args) throws IOException {
    String fileName = "examples/fm-screenshot.gif";

    RGBImage baseImage = RGBImage.load(fileName);

    LineRemover.removeLines(baseImage);

    int tileSize = 4;
    Tiles tiles = new Tiles(baseImage.toBW(), tileSize);

    RGBImage markedImage = new RGBImage(baseImage);
    markTiles(tiles, markedImage);

    SurfaceUtil.showAsMain("TextfindingTest", new JScrollPane(new ImageSurface(markedImage)));
  }

  private static void markTiles(Tiles tiles, RGBImage image) {
    int ts = tiles.getTileSize();
    for (int row = 0; row < tiles.getRows(); row++)
      for (int col = 0; col < tiles.getCols(); col++) {
        Tile tile = tiles.getTile(col, row);
        BWImage img = tiles.getImage(tile);
        Color color = null;

        double avg = img.averageBrightness();
        //System.out.println(avg);

        if (avg >= 0.99)
          color = Color.yellow;
        else {
          float min = OCRImageUtil.minBrightness(img);
          float max = OCRImageUtil.maxBrightness(img);
          if (min <= 0.1 && max >= 0.9)
            color = Color.blue;
        }

        if (color != null)
          ImageProcessing.drawRect(image, col*ts, row*ts, ts-1, ts-1, new RGB(color));
      }
  }

}
