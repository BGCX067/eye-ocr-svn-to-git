package eyedev._02;

import eyedev._01.ImageReader;
import eyedev._01.ImageReaderStream;
import eyedev._01.OCRUtil;

import java.util.ArrayList;
import java.util.List;

public class ImageReaderList implements Streamable {
  List<String> imageReaders = new ArrayList<String>();

  public ImageReaderList() {
  }

  public ImageReaderList(String imageReader) {
    imageReaders.add(imageReader);
  }

  public ImageReaderList(ImageReader imageReader) {
    add(imageReader);
  }

  public void add(ImageReader imageReader) {
    imageReaders.add(OCRUtil.getImageReaderDescription(imageReader));
  }

  public ImageReaderStream stream() {
    return OCRUtil.makeImageReaderStream(imageReaders);
  }
}
