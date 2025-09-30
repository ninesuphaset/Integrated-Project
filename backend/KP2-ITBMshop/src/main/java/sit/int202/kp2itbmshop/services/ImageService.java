package sit.int202.kp2itbmshop.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.kp2itbmshop.entities.Image;
import sit.int202.kp2itbmshop.entities.SaleItem;
import sit.int202.kp2itbmshop.repositories.ImageRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@Service
public class ImageService {
    private final Path uploadDir;
    private final ImageRepository imageRepository;

    public ImageService(@Value("${app.upload.dir:product-images}") String uploadDir,
                        ImageRepository imageRepository) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.imageRepository = imageRepository;
        try {
            if (!Files.exists(this.uploadDir)) {
                Files.createDirectories(this.uploadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create upload dir", e);
        }
    }

    public Image store(MultipartFile file, SaleItem saleItem, Integer order) {
        String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String storedName = saleItem.getId() + "." + originalName; //new name with saleItemId

        List<Image> existingImages = imageRepository.findBySaleItemIdOrderByPositionAsc(saleItem.getId());
        boolean duplicateInSameItem = existingImages.stream()
                .anyMatch(img -> img.getFileName().equalsIgnoreCase(storedName));

        if (duplicateInSameItem) {
            throw new RuntimeException("Duplicate file name not allowed in the same item: " + originalName);
        }

        Path target = this.uploadDir.resolve(storedName);

        try {
            if (!Files.exists(target)) {
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            }

            Image image = new Image();
            image.setSaleItem(saleItem);
            image.setFileName(storedName); // เก็บชื่อใหม่ใน db
            image.setPosition(order);

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Cannot store file: " + storedName, e);
        }
    }

    public void deleteFile(Image image) {
        try {
            Path path = this.uploadDir.resolve(image.getFileName()).normalize();
            if (Files.exists(path)) Files.delete(path);
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete file: " + image.getFileName(), e);
        }
    }

    public List<Image> getImagesBySaleItem(Integer saleItemId) {
        return imageRepository.findBySaleItemIdOrderByPositionAsc(saleItemId);
    }

    public void normalizePositions(List<Image> images) {
        if (images == null || images.isEmpty()) return;

        images.sort(Comparator.comparing(Image::getPosition));
        for (int i = 0; i < images.size(); i++) {
            Image img = images.get(i);
            img.setPosition(i + 1);
            imageRepository.save(img);
        }
    }

//    private Image findOrCreateImageEntity(SaleItem saleItem, Integer order) {
//        return imageRepository.findBySaleItemIdOrderByPositionAsc(saleItem.getId())
//                .stream()
//                .filter(i -> i.getPosition().equals(order))
//                .findFirst()
//                .orElseGet(() -> {
//                    Image n = new Image();
//                    n.setSaleItem(saleItem);
//                    n.setPosition(order);
//                    return n;
//                });
//    }

    public Resource loadFile(String fileName) {
        try {
            Path filePath = this.uploadDir.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file: " + fileName, e);
        }
    }
}
