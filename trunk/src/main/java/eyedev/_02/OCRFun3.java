package eyedev._02;

import eyedev._01.CT_ExampleSet;
import eyedev._01.DiscriminatorFinder;
import eyedev._01.ExampleSet;
import eyedev._01.PhotographicMemory;

public class OCRFun3 {
  public static void main(String[] args) {
    ExampleSet exampleSet = new CT_ExampleSet();

    ImageReaderList imageReaders = new ImageReaderList();

    imageReaders.add(new SingleBitDiscriminator(exampleSet));
    imageReaders.add(new PhotographicMemory(exampleSet));

    new DiscriminatorFinder(exampleSet, imageReaders.stream()).find();
  }
}
