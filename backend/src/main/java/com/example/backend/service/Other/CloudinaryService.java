package com.example.backend.service.Other;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.backend.dto.Response.Other.CloudinaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud_name}")
    private String cloudName;


    public CloudinaryResponse uploadImage(MultipartFile file) {
        // 1. Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép upload tệp tin hình ảnh!");
        }

        try {
            // 2. Send file to cloudinary and get response
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", cloudName,
                            "overwrite", true
                    ));

            // 3. Extract and return data
            return new CloudinaryResponse(
                    uploadResult.get("public_id").toString(),
                    uploadResult.get("secure_url").toString()
            );
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed : " + e.getMessage());
        }
    }

    public boolean deleteFile(String publicId) {
        // 1. Check public id
        if (publicId == null || publicId.isEmpty()) {
            return false;
        }
        // 2. Send public id to cloudinary for delete image uploaded
        try {
            Map result = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("invalidate", true, "resource_type", "image"));

            // 3. Return result
            return result.get("result").equals("success");
        } catch (IOException e) {
            System.err.println("Cloudinary Delete Error: " + e.getMessage());
            return false;
        }
    }
}