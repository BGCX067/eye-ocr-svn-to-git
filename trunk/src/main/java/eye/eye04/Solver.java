package eye.eye04;

import drjava.util.StringUtil;
import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._05.Alphabet;
import eyedev._08.FontLearner;
import eyedev._08.SegSigLearner;
import eyedev._09.WithAdvancedSegmenter;
import eyedev._09.WithFlexibleSegmenter;
import eyedev._10.PM2;
import eyedev._10.PM2Segmenter;

import java.awt.*;
import java.util.List;

public class Solver {
  public static void solveIt(final SolvingEnv env) {

    /*
    String solution = EyeStandardRecognizer.getDescription();
    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      Challenge challenge = list.get(i);
      env.setStatus("Trying standard recognizer for challenge " + (i+1));
      if (challenge.trySolution(solution))
        env.challengeUpdated(challenge);
    }
    */

    solveIL(env);
    solveRW(env);

    env.setStatus("");
  }

  private static void solveIL(final SolvingEnv env) {
    List<Challenge> list = env.getChallenges();
    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      Challenge _challenge = list.get(i);
      if (!(_challenge instanceof IndividualLettersChallenge)) continue;
      final IndividualLettersChallenge challenge = (IndividualLettersChallenge) _challenge;
      final String status = "Making custom recognizer for challenge " + (i + 1);
      env.setStatus(status);

      final FontLearner fontLearner = new SegSigLearner();
      for (Float size : challenge.getSizes())
        fontLearner.addFont(challenge.getFont().deriveFont(size));
      fontLearner.setProgressListener(new Runnable() {
        public void run() {
          float score = fontLearner.getToplist().getTopScore();
          env.setStatus(status + " (" + StringUtil.formatDouble(score*100, 1)+  "%)");
        }
      });
      fontLearner.go();

      String top = fontLearner.getBestRecognizer();
      if (top != null && challenge.trySolution(top))
        env.challengeUpdated(challenge);
    }
  }

  private static void solveRW(SolvingEnv env) {
    tryPM2Segmenters(env);
    makeSegmentersFromRecognizers(env);
  }

  private static void tryPM2Segmenters(SolvingEnv env) {
    List<Challenge> list = env.getChallenges();
    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      Challenge _challenge = list.get(i);
      if (!(_challenge instanceof RandomWordsChallenge)) continue;
      final RandomWordsChallenge challenge = (RandomWordsChallenge) _challenge;

      env.setStatus("Trying PM2Segmenter for challenge " + (i+1));

      ExampleSet exampleSet = Alphabet.makeAlphabet(challenge.getFont().deriveFont(30f));
      PM2 pm = new PM2(exampleSet);
      String solution = OCRUtil.getImageReaderDescription(new PM2Segmenter(pm));
      System.out.println(solution);
      env.trySolution(challenge, solution);
    }
  }

  private static void makeSegmentersFromRecognizers(SolvingEnv env) {
    List<Challenge> list = env.getChallenges();
    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      Challenge _challenge = list.get(i);
      if (!(_challenge instanceof RandomWordsChallenge)) continue;
      final RandomWordsChallenge challenge = (RandomWordsChallenge) _challenge;

      String charRecognizer = findCharacterRecognizer(challenge.getFont(), list);
      if (charRecognizer == null) continue;

      env.setStatus("Trying simple segmenter for challenge " + (i+1));
      ImageReader segmenter = new WithAdvancedSegmenter(charRecognizer);
      env.trySolution(challenge, OCRUtil.getImageReaderDescription(segmenter));

      env.setStatus("Trying flexible segmenter for challenge " + (i+1));
      segmenter = new WithFlexibleSegmenter(charRecognizer);
      env.trySolution(challenge, OCRUtil.getImageReaderDescription(segmenter));
    }
  }

  private static String findCharacterRecognizer(Font font, List<Challenge> challenges) {
    for (int i = 0, listSize = challenges.size(); i < listSize; i++) {
      Challenge _challenge = challenges.get(i);
      if (!(_challenge instanceof IndividualLettersChallenge)) continue;
      IndividualLettersChallenge challenge = (IndividualLettersChallenge) _challenge;
      if (!challenge.getFont().getName().equals(font.getName()))
        continue;
      return challenge.getSolution();
    }
    return null;
  }
}
