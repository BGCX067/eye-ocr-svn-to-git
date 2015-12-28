package eyedev._13;

import eye.eye05.RecognizableImage;
import eyedev._12.LineRemover;
import eyedev._12.Textfinder1;
import eyedev._12.TileCluster;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.ImageProcessing;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.util.*;

public class Whatever {
  private static int textClusterMargin = 4;

  public static void main(String[] args) {
    String fileName = "examples/fm-screenshot.gif";
    RGBImage baseImage = RGBImage.load(fileName);

    RGBImage image2 = new RGBImage(baseImage);
    LineRemover.removeLines(image2);

    Textfinder1 textFinder1 = new Textfinder1(image2, true);
    java.util.List<TileCluster> clusters = textFinder1.getClusters();

    RGBImage markedImage = new RGBImage(baseImage);
    for (TileCluster cluster : clusters) {
      Rectangle r = cluster.getBoundingRect();
      r.grow(textClusterMargin, textClusterMargin);
      ImageProcessing.drawRect(markedImage, r.x-1, r.y-1, r.width+1, r.height+1, new RGB(Color.blue));
    }

    SurfaceUtil.showAsMain("StartFromScratch", new RecognizableImage(markedImage));
  }
}
