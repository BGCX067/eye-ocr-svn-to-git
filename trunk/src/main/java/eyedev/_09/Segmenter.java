package eyedev._09;

import eyedev._01.DebugItem;
import eyedev._01.Processor;
import eyedev._17.MarkLine;
import prophecy.common.image.BWImage;

import java.util.ArrayList;
import java.util.List;

public abstract class Segmenter extends Processor {
  public abstract List<Segment> segment(BWImage baseImage);

  /** this only works when collectDebugInfo is true */
  public MarkLine getMarkLine(MarkLine.Type type) {
    for (DebugItem debugItem : getDebugItems(MarkLine.class))
      if (((MarkLine) debugItem.data).type == type)
        return (MarkLine) debugItem.data;
    return null;
  }
}
