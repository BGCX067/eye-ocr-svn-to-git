package eyedev._05;

import eyedev._01.ExampleSet;
import eyedev._04.Experiment_v2;
import eyedev._04.MultiStream;

import java.util.ArrayList;
import java.util.List;

public class Experiment_v3 extends Experiment_v2 {
  List<Strategy> strategies = new ArrayList<Strategy>();

  public Experiment_v3(ExampleSet exampleSet) {
    super(exampleSet);
  }

  public void addStrategy(Strategy strategy) {
    strategies.add(strategy);
  }

  protected void processMore(MultiStream multi) {
    for (Strategy strategy : strategies) {
      multi.add(strategy);
    }
  }
}
