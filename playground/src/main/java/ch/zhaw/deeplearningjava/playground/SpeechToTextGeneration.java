package ch.zhaw.deeplearningjava.playground;

import ai.djl.ModelException;
import ai.djl.training.util.DownloadUtils;
import ai.djl.translate.TranslateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public final class SpeechToTextGeneration {

    private static final Logger logger = LoggerFactory.getLogger(SpeechToTextGeneration.class);

    private SpeechToTextGeneration() {}

    public static void main(String[] args)
            throws ModelException, IOException, TranslateException, InterruptedException {
        DownloadUtils.download(
                "https://resources.djl.ai/audios/jfk.flac", "build/example/jfk.flac");

        try (WhisperModel model = new WhisperModel()) {
            /*
             * For this particular model, graph optimization takes long time for the first
             *  a couple of inference, and it doesn't improve for the following inference.
             */
            System.setProperty("ai.djl.pytorch.graph_optimizer", "false");
            logger.info(model.speechToText(Paths.get("build/example/jfk.flac")));
        } finally {
            System.clearProperty("ai.djl.pytorch.graph_optimizer");
        }
    }
}