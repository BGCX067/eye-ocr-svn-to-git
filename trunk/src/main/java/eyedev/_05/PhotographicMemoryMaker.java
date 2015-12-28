package eyedev._05;

import eyedev._01.*;
import eyedev._02.ImageReaderList;
import prophecy.common.image.BWImage;

public class PhotographicMemoryMaker extends Strategy {
  private int sizeLimit;

  public PhotographicMemoryMaker(int sizeLimit) {
    this.sizeLimit = sizeLimit;
  }

  public ImageReaderStream stream() {
    ImageReaderList list = new ImageReaderList();
    BWImage image = exampleSet.examples.get(0).image;
    if (image.getWidth()*image.getHeight()*exampleSet.examples.size() <= sizeLimit) {
      list.add(new PhotographicMemory(exampleSet));
    }
    return list.stream();
  }
}
