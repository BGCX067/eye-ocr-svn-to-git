package eye.eye03;

import eyedev._18.DB;
import prophecy.common.PersistentTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recognizers extends DB {
  public Recognizers() {
    
    setDir("memory/classdata");
    setFileNamePattern("recognizer{i}-{key}.eye");
  }

  public File save(RecognizerInfo info) throws IOException {
    File file = newFile(info.getName());
    saveFile(file, info.getTree());
    trigger.trigger();
    return file;
  }

  public List<RecognizerOnDisk> scan() throws IOException {
    List<RecognizerOnDisk> list = new ArrayList<RecognizerOnDisk>();
    for (File file : listFiles())
      try {
        list.add(new RecognizerOnDisk(file, new RecognizerInfo(loadFile(file)).getName()));
      } catch (Throwable e) {
        System.out.println("Skipping bad recognizer file: " + file.getPath());
      }
    return list;
  }

  // TODO (cosmetics): change file name
  public void rename(RecognizerOnDisk recognizer, String newName) throws IOException {
    PersistentTree tree = recognizer.getTree();
    new RecognizerInfo(tree).setName(newName);
    tree.save();
    trigger.trigger();
  }

  public void delete(RecognizerOnDisk recognizer) throws IOException {
    deleteFile(recognizer.file);
    trigger.trigger();
  }

  public RecognizerOnDisk findByName(String name) throws IOException {
    for (RecognizerOnDisk recognizerOnDisk : scan()) {
      if (recognizerOnDisk.getName().equals(name))
        return recognizerOnDisk;
    }
    return null;
  }
}
