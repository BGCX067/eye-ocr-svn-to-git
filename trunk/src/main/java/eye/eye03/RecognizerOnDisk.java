package eye.eye03;

import drjava.util.FileTreePersistence;
import drjava.util.FileUtil;
import drjava.util.Tree;
import prophecy.common.PersistentTree;

import java.io.File;
import java.io.IOException;

public class RecognizerOnDisk {
  File file;
  String name;

  public RecognizerOnDisk(File file, String name) {
    this.file = file;
    this.name = name;
  }

  public File getFile() {
    return file;
  }

  public String getName() {
    return name;
  }

  public PersistentTree getTree() throws IOException {
    return new PersistentTree(new FileTreePersistence(file, false));
  }

  public RecognizerInfo getRecognizerInfo() throws IOException {
    return new RecognizerInfo(getTree());
  }
}
