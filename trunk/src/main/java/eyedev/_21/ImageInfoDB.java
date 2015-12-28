package eyedev._21;

import eyedev._18.DB;

import java.io.File;
import java.io.IOException;

public class ImageInfoDB extends DB {
  public ImageInfoDB() {
    setDir("memory/classdata");
    setFileNamePattern("imageinfo{i}-{key}.eye");
  }

  public ImageInfo loadImageInfo(File imageFile) throws IOException {
    String imagePath = imageFile.getCanonicalPath();
    for (File file : listFiles()) {
      ImageInfo imageInfo = new ImageInfo(file, loadFile(file));
      if (imagePath.equals(imageInfo.getImagePath()))
        return imageInfo;
    }
    return null;
  }

  /** load or create image info for the specified image */
  public ImageInfo getImageInfo(File imageFile) throws IOException {
    ImageInfo imageInfo = loadImageInfo(imageFile);
    if (imageInfo == null) {
      String imagePath = imageFile.getCanonicalPath();
      File file = newFile(imagePath);
      imageInfo = new ImageInfo();
      imageInfo.setImagePath(imagePath);
      imageInfo.setFile(file);
      saveFile(file, imageInfo.getTree());
    }
    return imageInfo;
  }

  public void saveImageInfo(ImageInfo imageInfo) throws IOException {
    saveFile(imageInfo.getFile(), imageInfo.getTree());
  }
}
