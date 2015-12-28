package eyedev._01;

import eyedev._06.TrainSegmentSignature;
import eyedev._09.Subrecognition;
import eyedev._09.Translatable;

public class DebugItem {
  public String name;
  public Object data;

  public DebugItem(String name, Object data) {
    this.name = name;
    this.data = data;
  }

  public DebugItem translate(int x, int y) {
    if (data instanceof Translatable) {
      return new DebugItem(name, ((Translatable) data).translate(x, y));
    }
    return this;
  }
}
