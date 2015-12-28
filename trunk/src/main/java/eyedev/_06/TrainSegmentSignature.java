package eyedev._06;

import eyedev._01.ExampleSet;
import eyedev._01.OCRUtil;
import eyedev._05.Alphabet;

public class TrainSegmentSignature {
  public static void main(String[] args) {
    /*ExampleSet exampleSet = Alphabet.arial100();
    new ClassificationBySegments(exampleSet);
    System.out.println();
    new ClassificationBySegments(Alphabet.arial50());*/

    ExampleSet arial25 = Alphabet.arial25();
    ExampleSet arial50 = Alphabet.arial50();
    ExampleSet arial100 = Alphabet.arial100();

    System.out.println("Training on arial50.");
    SegmentSignature imageReader = new SegmentSignature(arial50);
    System.out.println("  => " + OCRUtil.getImageReaderDescriptionWithLength(imageReader));
    System.out.println("Score on arial25: " + OCRUtil.discriminatorScore(arial25, imageReader));
    System.out.println("Score on arial50: " + OCRUtil.discriminatorScore(arial50, imageReader));
    System.out.println("Score on arial100: " + OCRUtil.discriminatorScore(arial100, imageReader));

    System.out.println("Training on all three.");

    imageReader = new SegmentSignature(arial25);
    imageReader.train(arial50);
    imageReader.train(arial100);
    System.out.println("  => " + OCRUtil.getImageReaderDescriptionWithLength(imageReader));
    System.out.println("Score on arial50: " + OCRUtil.discriminatorScore(arial50, imageReader));
    System.out.println("Score on arial100: " + OCRUtil.discriminatorScore(arial100, imageReader));
    System.out.println("Score on arial25: " + OCRUtil.discriminatorScore(arial25, imageReader));

    System.out.println();
    System.out.println("Training on a host of sizes.");
    imageReader = new SegmentSignature();
    for (int size = 10; size <= 100; size += 5) {
      System.out.println("training " + size);
      imageReader.train(Alphabet.arial(size));
      imageReader.train(Alphabet.arialBold(size));
    }
    System.out.println("  => " + OCRUtil.getImageReaderDescriptionWithLength(imageReader));
    for (int size = 10; size <= 100; size += 5) {
      System.out.println("Score on arial" + size + ": "
        + OCRUtil.discriminatorScore(Alphabet.arial(size), imageReader));
      System.out.println("Score on arialbold" + size + ": "
        + OCRUtil.discriminatorScore(Alphabet.arialBold(size), imageReader));
    }
  }
}
