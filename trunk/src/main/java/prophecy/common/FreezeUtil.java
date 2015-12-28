package prophecy.common;

import drjava.util.Tree;
import drjava.util.Result;

public class FreezeUtil {
  public static Object unfreeze(Tree tree) {
    return new FrozenObject(tree).unfreeze().get();
  }

  public static Object unfreeze(String description) {
    return new FrozenObject(description).unfreeze().get();
  }

  public static String freeze(Freezable object) {
    return object.freeze().toString();
  }
}
