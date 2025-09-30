package sit.int202.kp2itbmshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.services.ImageService;

@RestController
@RequestMapping("/v2/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = imageService.loadFile(filename);
        String lower = filename.toLowerCase();

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (lower.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (lower.endsWith(".webp")) {
            mediaType = MediaType.valueOf("image/webp");
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(file);
    }
}
