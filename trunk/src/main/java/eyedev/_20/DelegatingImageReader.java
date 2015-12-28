package eyedev._20;

import drjava.util.Tree;
import eyedev._01.*;

public abstract class DelegatingImageReader extends ExtendedImageReader {
  protected ImageReader imageReader;

  public RecognizedText extendedReadImage(InputImage image) {
    return delegateRecognition(getImageReader(), image);
  }

  public CharacterLearner getCharacterLearner() {
    return getImageReader().getCharacterLearner();
  }

  public Tree toTree() {
    return getImageReader().toTree();
  }

  public ImageReader getImageReader() {
    if (imageReader == null)
      imageReader = makeImageReader();
    return imageReader;
  }

  public abstract ImageReader makeImageReader();
}
