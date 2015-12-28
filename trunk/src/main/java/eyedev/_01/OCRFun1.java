package eyedev._01;

import java.util.ArrayList;
import java.util.List;

public class OCRFun1 {
  public static void main(String[] args) {
    ExampleSet exampleSet = new CT_ExampleSet();

    List<String> imageReaders = new ArrayList<String>();
    imageReaders.add(OCRUtil.getImageReaderDescription(new CVersusT()));

    new DiscriminatorFinder(exampleSet, OCRUtil.makeImageReaderStream(imageReaders)).find();
  }
}
