package eyedev._16;

import eye.eye03.EyeEnv;
import eye.eye05.RecognizableImage;
import eyedev._12.TileCluster;
import eyedev._13.StandardDialog;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdvancedLineFinding {
  public static void main(String[] args) {
    EyeEnv.init();
    new AdvancedLineFinding();
  }

  AdvancedLineFinding() {
    System.out.println("Loading");
    RGBImage image = RGBImage.load("edinburgh-examples/poppy010.png");

    System.out.println("Creating text finder");
    TextFinder2 textFinder = new TextFinder2();

    // set parameters
    textFinder.setTileSize(4);
    textFinder.setMaxBlueGapToFill(64); // text contains multiple spaces
    textFinder.setMinimumClusterSize(new Dimension(16, 16));

    System.out.println("Finding clusters");
    textFinder.process(image.toBW());
    List<TileCluster> clusters = textFinder.getClusters();
    System.out.println(clusters.size() + " cluster(s) found");
    System.out.println("Marking image");
    RGBImage markedImage = image.copy();
    textFinder.markClustersWithGrayBackground(markedImage, clusters);

    System.out.println("Showing dialog");
    StandardDialog dialog = new StandardDialog("Advanced Line Finding");
    dialog.setExtendedState(JFrame.MAXIMIZED_BOTH);

    RecognizableImage recognizableImage = new RecognizableImage(markedImage);
    recognizableImage.setZoom(0.5);

    dialog.getMainPanel().add(recognizableImage);

    dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    dialog.centerAndShow();
  }
}
