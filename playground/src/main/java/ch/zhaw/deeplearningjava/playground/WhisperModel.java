package ch.zhaw.deeplearningjava.playground;


import ai.djl.ModelException;
import ai.djl.audio.translator.WhisperTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.modality.audio.Audio;
import ai.djl.modality.audio.AudioFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

import org.bytedeco.ffmpeg.global.avutil;

import java.io.IOException;
import java.nio.file.Path;

/** An example implementation of OpenAI Whisper Model. */
public class WhisperModel implements AutoCloseable {

    ZooModel<Audio, String> whisperModel;

    public WhisperModel() throws ModelException, IOException {
        Criteria<Audio, String> criteria =
                Criteria.builder()
                        .setTypes(Audio.class, String.class)
                        .optModelUrls(
                                "https://resources.djl.ai/demo/pytorch/whisper/whisper_en.zip")
                        .optEngine("PyTorch")
                        .optTranslatorFactory(new WhisperTranslatorFactory())
                        .build();
        whisperModel = criteria.loadModel();
    }

    public String speechToText(Audio audio) throws TranslateException {
        try (Predictor<Audio, String> predictor = whisperModel.newPredictor()) {
            return predictor.predict(audio);
        }
    }

    public String speechToText(Path file) throws IOException, TranslateException {
        Audio audio =
                AudioFactory.newInstance()
                        .setChannels(1)
                        .setSampleRate(16000)
                        .setSampleFormat(avutil.AV_SAMPLE_FMT_S16)
                        .fromFile(file);
        return speechToText(audio);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        whisperModel.close();
    }
}