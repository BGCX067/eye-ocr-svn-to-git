package eyedev._09;

import drjava.util.Function;
import drjava.util.MultiCoreUtil;
import drjava.util.Tree;
import eyedev._01.*;
import eyedev._16.TextCollector;
import eyedev._16.TextLocations;
import eyedev._17.MarkLine;
import eyedev._21.Corrections;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Segment and Recognize - splits image in lines, then in characters, then
 * applies character recognizer
 */
public class SaR extends ExtendedImageReader {

    private Logger l = Logger.getLogger(SaR.class);
    private Segmenter lineFinder, lineSegmenter;
    private ImageReader charRecognizer;
    // 0.22 is too little for accent-test.png. 0.3 is better, but still has a few too
    // many spaces.
    private float spaceThreshold = /*
             * 0.22f
             */ 0.35f;

    public SaR() {
    }

    /*
     * public SaR(String lineFinderDesc, String lineSegmenterDesc, String
     * charRecognizerDesc) { lineFinder =
     * OCRUtil.makeSegmenter(Tree.parse(lineFinderDesc)); lineSegmenter =
     * OCRUtil.makeSegmenter(Tree.parse(lineSegmenterDesc)); charRecognizer =
     * OCRUtil.makeImageReader(charRecognizerDesc); }
     */
    public SaR(Segmenter lineFinder, Segmenter lineSegmenter, ImageReader charRecognizer) {
        this.lineFinder = lineFinder;
        this.lineSegmenter = lineSegmenter;
        this.charRecognizer = charRecognizer;
    }

    @Override
    public RecognizedText extendedReadImage(InputImage inputImage) {
        BWImage image = inputImage.image;

        String charRecognizerDesc = charRecognizer.toString();
        SpaceRecognizer spaceRecognizer = new SpaceRecognizer(spaceThreshold);
        TextCollector textCollector = new TextCollector();

        setStatus("Finding lines");
        List<Segment> lines = lineFinder.segment(image);

        for (int iLine = 0, linesSize = lines.size(); iLine < linesSize; iLine++) {
            if (processCancelled()) {
                return null;
            }

            Segment line = lines.get(iLine);

            setStatus("Segmenting line " + (iLine + 1) + " / " + linesSize);
            lineSegmenter.setCollectDebugInfo(collectDebugInfo);
            List<Segment> letters = lineSegmenter.segment(line.segmentImage);
            addTranslatedDebugItems(lineSegmenter, line.boundingBox.x, line.boundingBox.y);
            MarkLine baseLine = lineSegmenter.getMarkLine(MarkLine.Type.base);
            MarkLine topLine = lineSegmenter.getMarkLine(MarkLine.Type.top);
            RecognizedLine recognizedLine = new RecognizedLine(line.boundingBox, "");

            setStatus("Recognizing line " + (iLine + 1) + " / " + linesSize);

            List<RecognizedText> texts = recognizeLetters(letters, baseLine, topLine, inputImage.corrections);

            for (int i = 0; i < letters.size(); i++) {
                try {
                    Segment letter = letters.get(i);

                    if (i > 0 && spaceRecognizer.isSpace(letters.get(i - 1).boundingBox, letter.boundingBox, baseLine, topLine)) {
                        textCollector.addText(" ", null);
                    }

                    RecognizedText text = texts.get(i);
                    if (text == null) {
                        text = new RecognizedText(null);
                    }
                    //System.out.println("text (len=" + text.text.length() + "): " + text.text);
                    Rectangle box = new Rectangle(letter.boundingBox);
                    box.translate(line.boundingBox.x, line.boundingBox.y); // translate to absolute coordinates
                    Subrecognition subrecognition = new Subrecognition(box, letter.segmentImage, charRecognizerDesc, text);
                    subrecognition.line = recognizedLine;
                    subrecognition.topLine = topLine.y - letter.boundingBox.y;
                    subrecognition.baseLine = baseLine.y - letter.boundingBox.y;

                    if (collectDebugInfo) {
                        addDebugItem(new DebugItem("Line " + (iLine + 1) + " letter " + (i + 1),
                                subrecognition));
                    }

                    textCollector.addText(text.text == null ? "?" : text.text, subrecognition);
                } catch (Exception e) {
                    l.debug(e.getMessage());
                }
            }

            recognizedLine.text = textCollector.getLine();
            addDebugItem("Line " + (iLine + 1), recognizedLine);

            textCollector.addLineBreak();
        }

        addDebugItem("Text locations", new TextLocations(textCollector.getLocations()));
        return new RecognizedText(textCollector.getText());
    }

    private void addTranslatedDebugItems(Processor processor, int x, int y) {
        List<DebugItem> debugInfo = processor.getDebugInfo();
        if (debugInfo != null) {
            for (DebugItem item : debugInfo) {
                addDebugItem(item.translate(x, y));
            }
        }
    }

    private List<RecognizedText> recognizeLetters(
            List<Segment> letters, final MarkLine baseLine, final MarkLine topLine,
            final Corrections corrections) {
        if (charRecognizer.isParallelizable()) {
            return MultiCoreUtil.parallelMap(letters, new Function<Segment, RecognizedText>() {

                public RecognizedText get(Segment letter) {
                    return recognizeLetter(letter, baseLine, topLine, corrections);
                }
            });
        } else {
            List<RecognizedText> texts = new ArrayList<RecognizedText>();
            for (Segment letter : letters) {
                texts.add(recognizeLetter(letter, baseLine, topLine, corrections));
            }
            return texts;
        }
    }

    private RecognizedText recognizeLetter(Segment letter, MarkLine baseLine,
            MarkLine topLine, Corrections corrections) {
        InputImage inputImage = new InputImage(letter.segmentImage);
        if (baseLine != null) {
            inputImage.baseLine = baseLine.y - letter.boundingBox.y;
        }
        if (topLine != null) {
            inputImage.topLine = topLine.y - letter.boundingBox.y;
        }
        if (corrections != null) {
            inputImage.corrections = corrections.clip(letter.boundingBox);
        }

        ImageReader charRecognizer = new ApplyCorrection(this.charRecognizer);
        return charRecognizer.extendedReadImage(inputImage);
    }

    public void fromTree(Tree tree) {
        lineFinder = OCRUtil.makeSegmenter(tree.get(0));
        lineSegmenter = OCRUtil.makeSegmenter(tree.get(1));
        charRecognizer = OCRUtil.makeImageReader(tree.get(2));
        spaceThreshold = tree.getFloat("spaceThreshold", spaceThreshold);
    }

    public Tree toTree() {
        return OCRUtil.treeFor(this).add(lineFinder.toTree()).add(lineSegmenter.toTree()).add(charRecognizer.toTree()).setFloat("spaceThreshold", spaceThreshold);
    }

    @Override
    public CharacterLearner getCharacterLearner() {
        return charRecognizer.getCharacterLearner();
    }

    @Override
    public void collectOptions(List<Option> options) {
        lineFinder.collectOptions(options);
        lineSegmenter.collectOptions(options);
        charRecognizer.collectOptions(options);
    }

    public float getSpaceThreshold() {
        return spaceThreshold;
    }

    public void setSpaceThreshold(float spaceThreshold) {
        this.spaceThreshold = spaceThreshold;
    }
}
