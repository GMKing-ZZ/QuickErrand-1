package com.quickerrand.controller;

import com.quickerrand.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8080}")
    private String urlPrefix;

    @ApiOperation("上传头像 - 按用户名分类存储")
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") @ApiParam("用户名") String username) {
        String relativePath = "avatars/" + username + "/" + generateFilename(file.getOriginalFilename());
        return saveFile(file, relativePath);
    }

    @ApiOperation("上传轮播图 - 统一存储在banners目录")
    @PostMapping("/upload/banner")
    public Result<String> uploadBanner(@RequestParam("file") MultipartFile file) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativePath = "banners/" + date + "/" + generateFilename(file.getOriginalFilename());
        return saveFile(file, relativePath);
    }

    @ApiOperation("上传评论图片 - 按订单号分类存储")
    @PostMapping("/upload/review")
    public Result<String> uploadReview(
            @RequestParam("file") MultipartFile file,
            @RequestParam("orderId") @ApiParam("订单ID") Long orderId) {
        String relativePath = "reviews/" + orderId + "/" + generateFilename(file.getOriginalFilename());
        return saveFile(file, relativePath);
    }

    @ApiOperation("上传跑腿员认证图片 - 按用户名分类存储")
    @PostMapping("/upload/runner-auth")
    public Result<String> uploadRunnerAuth(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") @ApiParam("用户名") String username) {
        String relativePath = "runner-auth/" + username + "/" + generateFilename(file.getOriginalFilename());
        return saveFile(file, relativePath);
    }

    @ApiOperation("上传订单物品图片 - 按订单号分类存储")
    @PostMapping("/upload/order-item")
    public Result<String> uploadOrderItem(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "orderNo", required = false) @ApiParam("订单号（可选）") String orderNo) {
        String relativePath;
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            relativePath = "order-items/" + orderNo + "/" + generateFilename(file.getOriginalFilename());
        } else {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            relativePath = "order-items/pending/" + date + "/" + generateFilename(file.getOriginalFilename());
        }
        return saveFile(file, relativePath);
    }

    @ApiOperation("通用上传图片（兼容旧接口）")
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        if (!isImageFile(file.getOriginalFilename())) {
            return Result.error("只支持上传图片文件（jpg、jpeg、png、gif）");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativePath = "common/" + date + "/" + generateFilename(file.getOriginalFilename());
        return saveFile(file, relativePath);
    }

    private Result<String> saveFile(MultipartFile file, String relativePath) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        if (!isImageFile(file.getOriginalFilename())) {
            return Result.error("只支持上传图片文件（jpg、jpeg、png、gif）");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }

        try {
            File destFile = new File(uploadPath + "/" + relativePath);
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            file.transferTo(destFile);

            String fileUrl = urlPrefix + "/uploads/" + relativePath;
            log.info("文件上传成功：{}", fileUrl);

            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    private boolean isImageFile(String filename) {
        if (filename == null) return false;
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals(".jpg") || extension.equals(".jpeg")
                || extension.equals(".png") || extension.equals(".gif");
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
}
