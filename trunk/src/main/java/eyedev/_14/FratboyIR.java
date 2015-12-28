package eyedev._14;

import drjava.util.GUIUtil;
import eye.eye03.EyeEnv;
import eye.eye03.Recognizers;
import eyedev._13.InteractiveRecognition;
import prophecy.common.image.RGBImage;

public class FratboyIR {
  public static void main(String[] args) {
    EyeEnv.init();

    String file = "other-examples/frat-boy.jpg";
    RGBImage image = RGBImage.load(file);

    // automatically uses Fratboy now
    InteractiveRecognition ir = new InteractiveRecognition(new Recognizers(), image, null);

    GUIUtil.showMainFrame(ir);
  }

}
