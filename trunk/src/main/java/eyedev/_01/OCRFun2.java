package eyedev._01;

import java.util.ArrayList;
import java.util.List;

public class OCRFun2 {

  // tests PhotographicMemory, trained on full example set

  public static void main(String[] args) {
    ExampleSet exampleSet = new ACT_ExampleSet();

    List<String> imageReaders = new ArrayList<String>();

    PhotographicMemory pm = new PhotographicMemory();
    pm.train(exampleSet);
    imageReaders.add(OCRUtil.getImageReaderDescription(pm));

    new DiscriminatorFinder(exampleSet, OCRUtil.makeImageReaderStream(imageReaders)).find();
  }
}
