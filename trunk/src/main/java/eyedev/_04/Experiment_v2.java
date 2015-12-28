package eyedev._04;

import eyedev._01.*;
import eyedev._02.ImageReaderList;
import eyedev._02.Streamable;
import eyedev._03.Experiment;
import prophecy.common.image.BWImage;

import java.util.ArrayList;
import java.util.List;

/**
 * An advanced type of experiment.
 *
 * Allows applying simplification operators
 */
public class Experiment_v2 extends Experiment {
  public List<Simplifier> simplifiers = new ArrayList<Simplifier>();
  public ExperimentMaker experimentMaker;

  public Experiment_v2(ExampleSet exampleSet) {
    super(exampleSet);
  }

  public void addSimplifier(Simplifier simplifier) {
    simplifiers.add(simplifier);
  }

  public void setExperimentMaker(ExperimentMaker experimentMaker) {
    this.experimentMaker = experimentMaker;
  }

  private static ExampleSet simplifyExampleSet(Simplifier simplifier, ExampleSet exampleSet) {
    ExampleSet newSet = new ExampleSet();
    for (Example example : exampleSet.examples) {
      BWImage image = simplifier.simplify(example.image);
      if (image == null)
        throw new RuntimeException("Broken simplifier: " + simplifier);
      newSet.add(image, example.text);
    }
    return newSet;
  }

  public ImageReaderStream makeStream() {
    MultiStream multi = new MultiStream();

    // standard stream (user-supplied image readers)
    multi.add(imageReaderList);

    processSimplifiers(multi);
    processMore();

    return multi.stream();
  }

  protected void processMore() {
  }

  private void processSimplifiers(MultiStream multi) {
    for (final Simplifier simplifier : simplifiers) {
      if (experimentMaker == null) {
        System.out.println("No experiment maker");
        break;
      }

      // simplify, run subexperiment
      multi.add(new Streamable() {
        public ImageReaderStream stream() {
          ExampleSet simplifiedExampleSet = simplifyExampleSet(simplifier, exampleSet);
          Experiment_v2 subexperiment = experimentMaker.makeExperiment(simplifiedExampleSet);
          String result = subexperiment.run();
          System.out.println("subexperiment result: " + result);
          if (result == null) return null;
          Simplify simplify = new Simplify(simplifier, OCRUtil.makeImageReader(result));
          return new ImageReaderList(simplify).stream();
        }
      });
    }
  }
}
