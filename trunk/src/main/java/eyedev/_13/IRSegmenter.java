package eyedev._13;

import eyedev._01.DebugItem;
import prophecy.common.image.BWImage;

import java.util.List;

public interface IRSegmenter {
  void setCharRecognizer(String description);
  void setCollectDebugInfo(boolean collectDebugInfo);
  List<DebugItem> getDebugInfo();
  String readImage(BWImage image);
  String rerecognize();
}
