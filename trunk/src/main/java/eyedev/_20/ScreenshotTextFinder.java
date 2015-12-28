package eyedev._20;

import eyedev._01.OCRImageUtil;
import eyedev._12.TileCluster;
import eyedev._12.TileType;
import eyedev._16.TextFinder2;
import prophecy.common.gui.AutoVMExit;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotTextFinder extends TextFinder2 {
  public ScreenshotTextFinder() {
    setTileSize(3);
    setMaxBlueGapToFill(4);
  }

  public List<TileCluster> filterClusters(List<TileCluster> clusters) {
    List<TileCluster> filteredList = new ArrayList<TileCluster>();
    for (TileCluster cluster : clusters) {
      Rectangle r = cluster.getBoundingRect();
      if (r.height > 24) continue;
      filteredList.add(cluster);
    }
    return filteredList;
  }

  public TileType getTileType_mt(BWImage img) {
    int numDark = OCRImageUtil.numPixelsDarkerThan(img, 0.4f);
    //System.out.println("num dark: " + numDark);
    if (numDark > 0)
      return TileType.blue; // possible text
    else
      return TileType.yellow; // no text
  }

  public static void main(String[] args) {
    RGBImage image = RGBImage.load("examples/foobar2000.png");
    ScreenshotTextFinder textFinder = new ScreenshotTextFinder();
    textFinder.process(image.toBW());
    OCRImageUtil.show(textFinder.getMarkedImage());
    AutoVMExit.install();
  }
}
