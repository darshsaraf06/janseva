package com.janseva.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.janseva.model.Complaint;
import com.janseva.model.User;
import com.janseva.repository.ComplaintRepository;
import com.janseva.repository.UserRepository;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint createComplaint(Complaint complaint, byte[] imageBytes, String username) throws IOException {
        // In a real app:
        // 1. Upload imageBytes to Google Cloud Storage or AWS S3.
        // 2. Get the public URL of the uploaded image.
        // 3. Set that URL to complaint.setPhotoUrl(imageUrl);
        complaint.setPhotoUrl("https://via.placeholder.com/300/FF0000/FFFFFF?text=Issue+Photo"); // Mock URL

        // 4. Analyze the image with Google Vision API.
        String detectedIssue = analyzeImage(imageBytes);
        complaint.setIssueType(detectedIssue);

        // 5. Associate with the logged-in user
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            complaint.setUser(user.get());
            
            // Award points to user for reporting (gamification)
            User foundUser = user.get();
            foundUser.setPoints(foundUser.getPoints() + 10); // 10 points per complaint
            userRepository.save(foundUser);
        } else {
            throw new RuntimeException("User not found: " + username);
        }

        return complaintRepository.save(complaint);
    }

    private String analyzeImage(byte[] imageBytes) throws IOException {
        // --- MOCK AI LOGIC ---
        // For this demo, we'll just randomly assign an issue type.
        // This simulates the AI classification without needing API keys.
        String[] possibleIssues = {"POTHOLE", "GARBAGE_DUMP", "BROKEN_STREETLIGHT", "WATER_LOGGING"};
        Random random = new Random();
        return possibleIssues[random.nextInt(possibleIssues.length)];

        /*
        // --- REAL GOOGLE VISION API LOGIC ---
        // Uncomment this block and add your credentials to use the real API.
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(List.of(request));
            AnnotateImageResponse res = response.getResponses(0);

            if (res.hasError()) {
                System.err.println("Error: " + res.getError().getMessage());
                return "UNCATEGORIZED";
            }

            // Simple logic: return the label with the highest score.
            // A real app would have more sophisticated mapping logic.
            EntityAnnotation annotation = res.getLabelAnnotations(0);
            return annotation.getDescription().toUpperCase().replace(" ", "_");
        }
        */
    }
}