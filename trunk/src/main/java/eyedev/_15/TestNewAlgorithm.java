package eyedev._15;

import eye.eye01.TextPainter2;
import eye.eye02.FontFinder;
import eye.eye04.EyeStandardCharacterRecognizers;
import eyedev._13.CompareImages;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGBImage;

public class TestNewAlgorithm {
  public static void main(String[] args) throws Exception {
    String text = "HALLO";
    BWImage bwImage = new TextPainter2(FontFinder.getEyeFont("Arial").loadFont(30f)).makeImage(text);

    CompareImages compareImages = (CompareImages) EyeStandardCharacterRecognizers.getAlpha7().getImageReader();
    NewAlgorithm newAlgorithm = new NewAlgorithm();
    newAlgorithm.pm = compareImages.getPM2();

    String result = newAlgorithm.readImage(bwImage);
    System.out.println("text:" + text + " - read: " + result);
    SurfaceUtil.showAsMain("New Algorithm", new ImageSurface(newAlgorithm.getMarkedImage(), 3.0));
  }
}
