package eyedev._12;

import eye.eye01.TestProtocolWindow;
import eyedev._01.ExampleSet;
import eyedev._07.RecognitionTest;
import eyedev._07.TestRunner;
import eyedev._09.FlexibleSegmenter;
import eyedev._09.Segment;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Textfinder1Test {
  private static int textClusterMargin = 4;

  public static void main(String[] args) throws IOException {
    String fileName = "other-examples/fm-screenshot.gif";

    RGBImage baseImage = RGBImage.load(fileName);

    Textfinder1 textFinder1 = new Textfinder1(baseImage, true);
    RGBImage markedImage = textFinder1.getMarkedImage();
    List<TileCluster> clusters = textFinder1.getClusters();

    List<BWImage> imagesToRecognize = new ArrayList<BWImage>();
    for (TileCluster cluster : clusters) {
      Rectangle r = cluster.getBoundingRect();
      ImageProcessing.drawRect(markedImage, r.x-1, r.y-1, r.width+1, r.height+1, new RGB(Color.black));
      r = new Rectangle(r);
      r.grow(textClusterMargin, textClusterMargin);
      BWImage image = baseImage.clip(r).toBW();
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
