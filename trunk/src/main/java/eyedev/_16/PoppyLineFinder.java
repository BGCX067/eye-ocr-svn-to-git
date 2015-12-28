package eyedev._16;

import eyedev._09.Segment;
import eyedev._09.Segmenter;
import eyedev._12.TileCluster;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.List;

public class PoppyLineFinder extends Segmenter {
  private List<TileCluster> clusters;

  public List<TileCluster> getClusters(BWImage image) {
    TextFinder2 textFinder = makeTextFinder();

    textFinder.process(image);
    return textFinder.getClusters();
  }

  public TextFinder2 makeTextFinder() {
    TextFinder2 textFinder = new TextFinder2();

    // set parameters
    textFinder.setTileSize(4);
    textFinder.setMaxBlueGapToFill(64); // text contains multiple spaces
    textFinder.setMinimumClusterSize(new Dimension(16, 16));
    return textFinder;
  }

  @Override
  public List<Segment> segment(BWImage baseImage) {
    TextFinder2 textfinder = makeTextFinder();
    return textfinder.segment(baseImage);
  }
}
