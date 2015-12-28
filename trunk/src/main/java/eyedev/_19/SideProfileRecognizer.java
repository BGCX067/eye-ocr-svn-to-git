package eyedev._19;

import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;
import prophecy.common.image.BWImage;

public class SideProfileRecognizer extends AbstractSignatureMulti {
  public SideProfileRecognizer() {}
  public SideProfileRecognizer(ExampleSet exampleSet) {
    train(exampleSet);
  }

  @Override
  public String getSignature(BWImage image) {
    String sig = "";
    for (int i = 0; i < 4; i++) {
      String s = new SideProfileMaker(image, i).getSignature();
      if (sig.length() == 0) sig = s; else sig += "-" + s;
    }
    return sig;
  }
}
