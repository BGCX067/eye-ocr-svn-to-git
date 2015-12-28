package eyedev._14;

import eye.eye01.EyeGuiUtil;
import eyedev._01.OCRImageUtil;
import eyedev._09.FlexibleSegmenter;
import eyedev._11.FlexibleSegmentationVisualizer;
import prophecy.common.gui.AutoVMExit;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import javax.swing.*;

public class GrayscaleSegmentation {
  public static void main(String[] args) {
    BWImage image = RGBImage.load("other-examples/frat-boy.jpg").toBW();

    // "Frat boy"
    //image = image.clip(0, 0, image.getWidth(), 60);

    image = image.clip(0, 305, image.getWidth(), 30);

    image = OCRImageUtil.trim(image);

    FlexibleSegmentationVisualizer.FlexibleSegmentationOptions options = new FlexibleSegmentationVisualizer.FlexibleSegmentationOptions();
    options.threshold = 0.75f;
    FlexibleSegmentationVisualizer.show(image, options);
    AutoVMExit.install();

    //EyeGuiUtil.showImage(image).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
