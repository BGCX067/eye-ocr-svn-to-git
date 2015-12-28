package eyedev._04;

import eyedev._01.Describable;
import prophecy.common.image.BWImage;

public interface Simplifier extends Describable {
  BWImage simplify(BWImage image);
}
