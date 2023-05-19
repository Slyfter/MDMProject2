package ch.zhaw.deeplearningjava.playground;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
public class SpeechToTextController {

    private WhisperModel whisperModel;
    private Path tempFile;

    public SpeechToTextController() throws IOException, ModelException {
        whisperModel = new WhisperModel();
    }

    @PostMapping(value = "/speechToText", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertSpeechToText(@RequestParam("file") MultipartFile file) throws IOException, TranslateException {
        // Delete the previous temporary file, if any
        deleteTempFile();

        // Create a new temporary file for the current request
        tempFile = Files.createTempFile("speechToText", ".wav");
        try {
            // Save the uploaded file to the temporary file
            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Process the current file
            String text = whisperModel.speechToText(tempFile);
            return ResponseEntity.ok(text);
        } finally {
            // Delete the temporary file after processing
            deleteTempFile();
        }
    }

    private void deleteTempFile() {
        if (tempFile != null) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                // Handle or log any exception during file deletion
                e.printStackTrace();
            } finally {
                tempFile = null;
            }
        }
    }
}
