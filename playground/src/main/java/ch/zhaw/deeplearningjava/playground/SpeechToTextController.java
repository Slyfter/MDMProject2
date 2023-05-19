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

    @PostMapping(value = "/speechToText", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertSpeechToText(@RequestParam("file") MultipartFile file) throws IOException, TranslateException, ModelException {
        try (// Create a new WhisperModel instance for each request
        WhisperModel whisperModel = new WhisperModel()) {
            // Create a temporary file for the current request
            Path tempFile = Files.createTempFile("speechToText", ".wav");
            try {
                // Save the uploaded file to the temporary file
                try (var inputStream = file.getInputStream()) {
                    Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
   
                // Process the current file
                String text = whisperModel.speechToText(tempFile);
   
                // Remove the last whitespace character and the last dot from the result
                if (text.endsWith(" ")) {
                    text = text.substring(0, text.length() - 1);
                }
                if (text.endsWith(".")) {
                    text = text.substring(0, text.length() - 1);
                }
   
                return ResponseEntity.ok(text);
            } finally {
                // Delete the temporary file after processing
                Files.deleteIfExists(tempFile);
            }
        }
    }
}
