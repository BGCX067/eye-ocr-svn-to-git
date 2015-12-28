package eyedev._05;

import eyedev._01.Example;
import eyedev._01.ExampleSet;

import java.awt.*;

public class OCRFun6 {
  public static void main(String[] args) {
    Font font = new Font("Arial", Font.PLAIN, 100);

    /*BWImage img = TextPainter.paintText(150, 150, 40, 100, font, "X");
    SurfaceUtil.showAsMain("OCRFun6", new ImageSurface(img));*/

    ExampleSet alphabet = Alphabet.makeAlphabet(font, 150, 150, 40, 100);
    Experiment_v3 experiment = new Experiment_v3(alphabet);
    experiment.addStrategy(new PhotographicMemoryMaker(100000));

    /*experiment.addStrategy(new Strategy() {
      public ImageReaderStream stream() {
        ImageReaderList list = new ImageReaderList();
        return list.stream();
      }
    });*/
    experiment.run();

    for (Example example : alphabet.examples) {
      String s1 = SegmentCounter.getSegmentCount(example.image, false);
      String s2 = SegmentCounter.getSegmentCount(example.image, true);
      System.out.println(example.text + ": " + s1 + " " + s2);
    }
  }
}
