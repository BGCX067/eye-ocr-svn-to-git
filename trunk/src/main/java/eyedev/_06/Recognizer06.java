package eyedev._06;

import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;

/* the Arial character recognizer */
public class Recognizer06 {
  public static ImageReader makeRecognizer() {
    return OCRUtil.makeImageReader(getRecognizerDesc());
  }

  public static String getRecognizerDesc() {
    return "_06.FixAVersusR _06.FixSVersusG(_06.FixUVersusV _06.FixVVersusY(_06.FixLVersusE _06.FixDVersusP(_06.FixIVersusT _06.FixOVersusP(_06.SegmentSignature #(\"1-1\", T, \"1-12\", F, \"1-121\", F, \"1-1232\", Z, \"1-12321\", Z, \"1-13\", E, \"1-131\", E, \"1-132\", E, \"1-1321\", E, \"1-232\", Z, \"1-2321\", Z, \"121-1\", J, \"121-121\", P, \"121-1231\", Q, \"121-12321\", Q, \"1212-121\", A, \"1212-12121\", R, \"1212-12321\", R, \"12121-12\", C, \"12121-1232\", G, \"12121-12321\", S, \"12121-1312\", B, \"12121-132\", B, \"12121-1321\", B, \"12121-132121\", B, \"12121-134321\", B, \"12121-232\", S, \"12121-2321\", S, \"1232121-12321\", Q, \"2-12\", K, \"21-1\", Y, \"212-1\", H, \"212-12\", K, \"212-121\", K, \"212-1212\", X, \"212-12121\", X, \"212-212\", X, \"232-1\", N, \"23432-1\", M, \"243-1\", M, \"2432-1\", M, \"312-1\", W, \"342-1\", W, \"3432-1\", W)))))";
  }
}
