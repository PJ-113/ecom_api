package com.ecom2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class StorageService {

  @Value("${app.upload.dir}")
  private String uploadDir;

  public String saveImage(MultipartFile file) {
    if (file == null || file.isEmpty()) return null;
    try {
      Path dir = Paths.get(uploadDir);
      if (!Files.exists(dir)) Files.createDirectories(dir);
      String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+","_");
      Path target = dir.resolve(filename);
      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
      return "/uploads/" + filename; // URL ที่เปิดดูได้
    } catch (IOException e) {
      throw new RuntimeException("Cannot store file", e);
    }
  }
}

