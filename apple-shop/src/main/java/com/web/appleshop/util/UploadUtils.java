package com.web.appleshop.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.web.appleshop.exception.IllegalArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class UploadUtils {
    public static final String UPLOAD_DIR = "./uploads/";
    public static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final long MAX_FILE_SIZE = 1024 * 1024 * 20; // 20MB

    public static boolean isAllowedExtension(String filename) {
        for (String extension : ALLOWED_EXTENSIONS) {
            if (filename.toLowerCase().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllowedSize(long size) {
        return size <= MAX_FILE_SIZE;
    }

    public String uploadFile(MultipartFile file) {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được bỏ trống.");
        }

        if (!isAllowedExtension(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Phần mở rộng của file ảnh không được hỗ trợ." + file.getName() + file.getOriginalFilename());
        }

        if (!isAllowedSize(file.getSize())) {
            throw new IllegalArgumentException("Dung lượng file ảnh không được vượt quá " + MAX_FILE_SIZE / 1024 / 1024 + "MB.");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())).startsWith("blob:") ? file.getOriginalFilename().substring(5) : file.getOriginalFilename().replaceAll(" ", "_");

        if (filename.contains("..")) {
            throw new IllegalArgumentException("Tên file ảnh không hợp lệ.");
        }

        String uniqueFilename = UUID.randomUUID() + "_" + filename;

        try {
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Lỗi khi lưu file ảnh.", e);
        }

        System.out.println("Uploaded file successfully: " + ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/"+uniqueFilename).toUriString());

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/"+uniqueFilename).toUriString();
    }
}
