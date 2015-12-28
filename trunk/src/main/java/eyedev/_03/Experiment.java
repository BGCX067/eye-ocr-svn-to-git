package eyedev._03;

import eyedev._01.DiscriminatorFinder;
import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;
import eyedev._01.ImageReaderStream;
import eyedev._02.ImageReaderList;

/*
 * A simple type of discriminator finding experiment.
 *
 * (example set + fixed set of image readers)
 */
public class Experiment {
  public ExampleSet exampleSet;
  public ImageReaderList imageReaderList = new ImageReaderList();

  public Experiment(ExampleSet exampleSet) {
    this.exampleSet = exampleSet;
  }

  public String run() {
    return new DiscriminatorFinder(exampleSet, makeStream()).find();
  }

  /** override me */
  public ImageReaderStream makeStream() {
    return imageReaderList.stream();
  }

  public void addImageReader(ImageReader imageReader) {
    imageReaderList.add(imageReader);
  }
}
