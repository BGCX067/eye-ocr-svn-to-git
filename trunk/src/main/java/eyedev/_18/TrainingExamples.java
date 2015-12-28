package eyedev._18;

import prophecy.common.image.RGBImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrainingExamples extends DB {
  public TrainingExamples() {
    setDir("memory/training-examples");
    setFileNamePattern("te{i}-{key}.eye");
  }

  public TrainingExampleOnDisk save(TrainingExample trainingExample) throws IOException {
    File file = newFile(trainingExample.getOriginalImage());
    TrainingExampleOnDisk onDisk = new TrainingExampleOnDisk(file, trainingExample);
    save(onDisk);
    return onDisk;
  }

  public void save(TrainingExampleOnDisk onDisk) throws IOException {
    saveFile(onDisk.getFile(), onDisk.getTrainingExample().getTree());
  }

  public List<TrainingExampleOnDisk> scan() throws IOException {
    List<TrainingExampleOnDisk> list = new ArrayList<TrainingExampleOnDisk>();
    for (File file : listFiles())
      list.add(new TrainingExampleOnDisk(file, new TrainingExample(loadFile(file))));
    return list;
  }

  public void delete(TrainingExampleOnDisk onDisk) throws IOException {
    deleteFile(onDisk.file);
    File imageFile = new File(dir, onDisk.getTrainingExample().getImageName());
    if (!imageFile.delete())
      throw new IOException("Could not delete " + imageFile.getPath());
    trigger.trigger();
  }

  public RGBImage loadImage(TrainingExample trainingExample) {
    return RGBImage.load(getImageFile(trainingExample));
  }

  public File getImageFile(TrainingExample trainingExample) {
    return new File(dir, trainingExample.getImageName());
  }
}
