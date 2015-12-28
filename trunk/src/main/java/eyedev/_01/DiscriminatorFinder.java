package eyedev._01;

public class DiscriminatorFinder {
  private ExampleSet exampleSet;
  private ImageReaderStream imageReaderStream;
  private String bestResult;

  public DiscriminatorFinder(ExampleSet exampleSet, ImageReaderStream imageReaderStream) {
    this.exampleSet = exampleSet;
    this.imageReaderStream = imageReaderStream;
  }

  public String find() {
    while (true) {
      String description = imageReaderStream.getNextImageReaderDescription();
      if (description == null)
        break;

      if (bestResult == null || description.length() < bestResult.length()) {
        System.out.println("Trying: " + description);

        if (works(description)) {
          bestResult = description;
          System.out.println("New best result! (" + description.length() + " chars)");
        }
      }
    }

    printResult();
    return bestResult;
  }

  private void printResult() {
    if (bestResult == null)
      System.out.println("No discriminator found for " + exampleSet);
    else
      System.out.println("Discriminator found with l=" + bestResult.length() + " for " + exampleSet + ": " + bestResult);
  }

  private boolean works(String description) {
    return OCRUtil.discriminatorWorks(exampleSet, OCRUtil.makeImageReader(description));
  }
}
