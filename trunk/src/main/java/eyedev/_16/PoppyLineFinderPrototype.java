package eyedev._16;

import eyedev._12.TileCluster;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.util.List;

public class PoppyLineFinderPrototype {
  private List<TileCluster> clusters;
  private RGBImage image;

  public void loadImageAndProcess() {
    System.out.println("Loading image");
    image = RGBImage.load("edinburgh-examples/poppy010.png");

    System.out.println("Creating text finder");
    TextFinder2 textFinder = new TextFinder2();

    // set parameters
    textFinder.setTileSize(4);
    textFinder.setMaxBlueGapToFill(64); // text contains multiple spaces
    textFinder.setMinimumClusterSize(new Dimension(16, 16));

    System.out.println("Finding clusters");
    textFinder.process(image.toBW());
    clusters = textFinder.getClusters();
  }

  public List<TileCluster> getClusters() {
    return clusters;
  }

  public RGBImage getImage() {
    return image;
  }
}
