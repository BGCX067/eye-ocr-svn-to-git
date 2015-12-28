package eyedev._18;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrainingExampleOnDisk {
  File file;
  TrainingExample testCase;

  public TrainingExampleOnDisk(File file, TrainingExample testCase) {
    this.file = file;
    this.testCase = testCase;
  }

  public File getFile() {
    return file;
  }

  public TrainingExample getTrainingExample() {
    return testCase;
  }

  /*public String getID() {
    Matcher matcher = Pattern.compile("\\d+").matcher(file.getName());
    matcher.find();
    return matcher.group();
  }*/
}
