package eyedev._16;

import eye.eye01.TestProtocolWindow;
import eyedev._01.ExampleSet;
import eyedev._07.RecognitionTest;
import eyedev._07.TestRunner;
import eyedev._09.FlexibleSegmenter;
import eyedev._09.Segment;
import eyedev._12.ExampleSetRecognitionTest;
import eyedev._12.Textfinder1;
import eyedev._12.TileCluster;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PoppyLineFinderTest {
  private static int textClusterMargin = 4;

  public static void main(String[] args) throws IOException {
    String fileName = "edinburgh-examples/poppy010.png";

    BWImage baseImage = RGBImage.load(fileName).toBW();

    PoppyLineFinder lineFinder = new PoppyLineFinder();
    TextFinder2 textFinder = lineFinder.makeTextFinder();
    textFinder.process(baseImage);
    List<TileCluster> clusters = textFinder.getClusters();
    RGBImage markedImage = textFinder.getMarkedImage();

    List<BWImage> imagesToRecognize = new ArrayList<BWImage>();
    for (TileCluster cluster : clusters) {
      Rectangle r = cluster.getBoundingRect();
      ImageProcessing.drawRect(markedImage, r.x-1, r.y-1, r.width+1, r.height+1, new RGB(Color.black));
      r = new Rectangle(r);
      r.grow(textClusterMargin, textClusterMargin);
      BWImage image = baseImage.clip(r);
      float threshold = 0.5f;
      image = ImageProcessing.threshold(image, threshold);
      imagesToRecognize.add(image);
    }

    /*
    imagesToRecognize = splitIntoCharacters(imagesToRecognize);
    String recognizerDesc = EyeStandardLineRecognizer.arial89Percent();
    ImageReader recognizer = OCRUtil.makeImageReader(recognizerDesc);
    showProtocol(imagesToRecognize, recognizerDesc);
    */

    showImage(markedImage);
  }

  private static void showImage(RGBImage markedImage) {
    SurfaceUtil.showAsMain("Textfinder1", new JScrollPane(new ImageSurface(markedImage)));
  }

  private static void showProtocol(List<BWImage> imagesToRecognize, String recognizerDesc) {
    ExampleSet exampleSet = new ExampleSet();
    for (BWImage image : imagesToRecognize)
      exampleSet.add(image, "");

    RecognitionTest test = new ExampleSetRecognitionTest(exampleSet);
    TestRunner testRunner = new TestRunner(test, recognizerDesc);
    testRunner.run();
    TestProtocolWindow protocolWindow = TestProtocolWindow.show(testRunner.getProtocol(), recognizerDesc);
    protocolWindow.setZoom(2.0);
    protocolWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private static List<BWImage> splitIntoCharacters(List<BWImage> images) {
    List<BWImage> newList = new ArrayList<BWImage>();
    for (BWImage image : images) {
      for (Segment letter : new FlexibleSegmenter().segment(image)) {
        newList.add(letter.segmentImage);
      }
    }
    return newList;
  }
}
