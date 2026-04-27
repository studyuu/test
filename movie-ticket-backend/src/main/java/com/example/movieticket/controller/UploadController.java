package com.example.movieticket.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    // 上传文件的保存路径
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads"
            + File.separator;

    @PostMapping
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                response.put("code", 400);
                response.put("message", "上传文件不能为空");
                return response;
            }

            // 检查上传目录是否存在，不存在则创建
            File uploadDir = new File(UPLOAD_DIR);
            System.out.println("上传目录: " + uploadDir.getAbsolutePath());
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("创建上传目录: " + created);
            }

            // 检查上传目录是否可写
            if (!uploadDir.canWrite()) {
                response.put("code", 500);
                response.put("message", "上传目录不可写");
                return response;
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            System.out.println("原始文件名: " + originalFilename);
            if (originalFilename == null || !originalFilename.contains(".")) {
                response.put("code", 400);
                response.put("message", "文件名无效");
                return response;
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            System.out.println("生成的文件名: " + fileName);

            // 保存文件
            File destFile = new File(UPLOAD_DIR + fileName);
            System.out.println("保存路径: " + destFile.getAbsolutePath());
            file.transferTo(destFile);
            System.out.println("文件保存成功");

            // 构建文件访问URL
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            System.out.println("文件访问URL: " + fileUrl);

            // 返回成功响应
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", Map.of("url", fileUrl, "filename", fileName));
        } catch (Exception e) {
            // 返回失败响应
            response.put("code", 500);
            response.put("message", "上传失败");
            response.put("error", e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}