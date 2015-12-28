package eyedev._03;

import drjava.util.Randomizer;
import drjava.util.RealRandomizer;
import eyedev._01.Example;
import eyedev._01.ExampleSet;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

public class RandomPlacement {
  private static Randomizer randomizer = new RealRandomizer();

  public static ExampleSet transform(int width, int height, ExampleSet baseExampleSet) {
    ExampleSet newSet = new ExampleSet();
    for (Example example : baseExampleSet.examples) {
      newSet.add(randomPlacement(width, height, example.image), example.text);
    }
    return newSet;
  }

  private static BWImage randomPlacement(int width, int height, BWImage image) {
    int x = randomizer.random(width-image.getWidth());
    int y = randomizer.random(height-image.getHeight());
    BWImage newImage = new BWImage(width, height, 1f);
    ImageProcessing.copy(image, 0, 0, newImage, x, y, image.getWidth(), image.getHeight());
    return newImage;
  }
}
