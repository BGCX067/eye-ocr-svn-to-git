package eyedev._10;

import eyedev._01.*;

import java.util.ArrayList;
import java.util.List;

public class TestPM2 {

  // tests PM2

  public static void main(String[] args) {
    ExampleSet exampleSet = new ACT_ExampleSet();

    List<String> imageReaders = new ArrayList<String>();

    PM2 pm = new PM2(exampleSet);
    imageReaders.add(OCRUtil.getImageReaderDescription(pm));

    new DiscriminatorFinder(exampleSet, OCRUtil.makeImageReaderStream(imageReaders)).find();
  }
}
