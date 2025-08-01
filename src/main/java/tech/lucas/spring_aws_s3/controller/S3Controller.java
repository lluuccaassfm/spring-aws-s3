package tech.lucas.spring_aws_s3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.lucas.spring_aws_s3.service.S3Service;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {
    Logger log = LoggerFactory.getLogger(S3Controller.class);

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload/{bucket}")
    public ResponseEntity<String> uploadFile(@PathVariable String bucket, @RequestParam("file") MultipartFile file) {
        try {
            s3Service.uploadFile(bucket, file);
            return ResponseEntity.ok("File uploaded successfully to bucket: " + bucket);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }

    @GetMapping("/download/{bucket}/{key}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String bucket, @PathVariable String key) {
        try {
            byte[] data = s3Service.downloadFile(bucket, key);
            log.info("Stream do arquivo '{}' realizado com sucesso!", key);
            return ResponseEntity.ok().body(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new byte[0]);
        }
    }
}
