package com.jash.taskservice.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jash.taskservice.core.exception.BusinessRuleException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class UserDocumentController {

    private final UserDocumentRepository documentRepository;
    private final UserMasterRepository userRepository;

    // Define dedicated local storage location path for uploaded mobile content binary parts
    private static final String UPLOAD_DIR = "D:/01_Workspace/pg_premium_suite/storage/uploads/";

    public UserDocumentController(UserDocumentRepository documentRepository, UserMasterRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<UserDocument> handleMobileFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            Principal principal) {

        if (file.isEmpty()) {
            throw new BusinessRuleException("Cannot upload an empty file structure.");
        }

        // 1. Identify the logged-in User profile through the Spring Security context Principal
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new BusinessRuleException("User profile not found for identity: " + principal.getName()));

        try {
            // 2. Ensure target storage directory folder boundaries exist on disk
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 3. Generate a collision-free filename to keep multiple devices from overwriting each other
            String cleanOriginalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
            String uniqueFileName = UUID.randomUUID().toString() + "_" + cleanOriginalName;
            Path destinationPath = Paths.get(UPLOAD_DIR + uniqueFileName);

            // 4. Stream input stream bytes straight down into local folder path
            Files.copy(file.getInputStream(), destinationPath);

            // 5. Build up metadata tracing ledger entry maps to flag file trace as pending verification
            UserDocument documentRecord = new UserDocument();
            documentRecord.setUserId(user.getId());
            documentRecord.setDocumentType(documentType.toUpperCase());
            documentRecord.setFileName(uniqueFileName);
            documentRecord.setFileStoragePath(destinationPath.toAbsolutePath().toString());
            documentRecord.setContentType(file.getContentType());
            documentRecord.setVerified(false);

            UserDocument savedRecord = documentRepository.save(documentRecord);
            return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);

        } catch (IOException e) {
            throw new BusinessRuleException("Failed to stream upload content binary components onto storage layer: " + e.getMessage());
        }
    }
}