package eyedev._09;

import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Segment {
  public SegmentLevel level;
  public Rectangle boundingBox;
  public BWImage segmentImage; // image of the segment (not whole image segment was extracted out of)
  public List<Segment> subsegments = new ArrayList<Segment>();

  public Segment(SegmentLevel level, Rectangle boundingBox, BWImage segmentImage) {
    this.level = level;
    this.segmentImage = segmentImage;
    this.boundingBox = boundingBox;
  }
}
