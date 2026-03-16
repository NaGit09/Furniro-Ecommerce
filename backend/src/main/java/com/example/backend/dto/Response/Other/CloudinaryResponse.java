package com.example.backend.dto.Response.Other;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudinaryResponse {
    private String publicId;
    private String url;
}
