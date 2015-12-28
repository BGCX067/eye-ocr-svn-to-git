package eye.eye04;

import java.util.List;

public interface SolvingEnv {
  void challengeUpdated(Challenge challenge);
  void setStatus(String status);
  List<Challenge> getChallenges();
  void trySolution(Challenge challenge, String solution);
}
