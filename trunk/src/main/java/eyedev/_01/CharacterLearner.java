package eyedev._01;

import prophecy.common.image.BWImage;

import java.util.TreeSet;

public interface CharacterLearner {
  /** learn a character */
  void learnCharacter(ImageWithMarkLines image, String text);

  /** get the set of known characters */
  TreeSet<String> getKnownCharacters();

  /** get known characters plus images */
  ExampleSet getExampleSet();
}
