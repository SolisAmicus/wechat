package com.solisamicus.controller;

import com.solisamicus.config.MinIOConfig;
import com.solisamicus.constants.QrCodeConstants;
import com.solisamicus.constants.VideoCoverConstants;
import com.solisamicus.exceptions.GraceException;
import com.solisamicus.feign.UserInfoMicroServiceFeign;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.vo.UserVO;
import com.solisamicus.pojo.vo.VideoMsgVO;
import com.solisamicus.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.solisamicus.constants.Properties.*;
import static com.solisamicus.constants.Symbols.*;
import static com.solisamicus.constants.VideoCoverConstants.DEFAULT_DIR_PATH;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private MinIOConfig minIOConfig;

    @Autowired
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        String filename = file.getOriginalFilename();
        filename = FileUtils.generateFilenameWithUUIDOnly(filename);
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // face/{userId}/{uuid}.png
        filename = String.format("%s%s%s%s%s", FACE, SLASH, userId, SLASH, filename);
        try {
            MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        String faceURL = MinIOUtils.getFileAccessUrl(filename);
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateFace(userId, faceURL);
        UserVO userVO = JsonUtils.jsonToPojo(JsonUtils.objectToJson(jsonResult.getData()), UserVO.class);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("generatorQrCode")
    public String generatorQrCode(@RequestParam("userId") String userId, @RequestParam("wechatNumber") String wechatNumber) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("wechatNumber", wechatNumber);
        String qrCodePath = QrCodeUtils.generateQRCode(JsonUtils.objectToJson(map));
        if (StringUtils.isNotBlank(qrCodePath)) {
            String uuid = UUID.randomUUID().toString();
            // qrcode/{userId}/{uuid}.png
            String filename = String.format("%s%s%s%s%s%s%s", QRCODE, SLASH, userId, SLASH, uuid, DOT, QrCodeConstants.IMAGE_FORMAT);
            String qrCodeUrl = "";
            try {
                qrCodeUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, qrCodePath, true);
            } catch (Exception e) {
                GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
            }
            return qrCodeUrl;
        }
        return null;
    }

    @PostMapping("uploadFriendCircleBg")
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        String friendCircleBgURL = uploadFiles(file, userId, FRIEND_CIRCLE, BG);
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateFriendCircleBg(userId, friendCircleBgURL);
        UserVO userVO = JsonUtils.jsonToPojo(JsonUtils.objectToJson(jsonResult.getData()), UserVO.class);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("uploadChatBg")
    public GraceJSONResult updateChatBg(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        String chatBgURL = uploadFiles(file, userId, CHAT, BG);
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateChatBg(userId, chatBgURL);
        UserVO userVO = JsonUtils.jsonToPojo(JsonUtils.objectToJson(jsonResult.getData()), UserVO.class);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("uploadFriendCircleImage")
    public GraceJSONResult uploadFriendCircleImage(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        return GraceJSONResult.ok(uploadFiles(file, userId, FRIEND_CIRCLE, IMAGES));
    }

    @PostMapping("uploadChatPhoto")
    public GraceJSONResult uploadChatPhoto(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        return GraceJSONResult.ok(uploadFiles(file, userId, CHAT, PHOTO));
    }

    @PostMapping("uploadChatVideo")
    public GraceJSONResult uploadChatVideo(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        String VideoURL = uploadFiles(file, userId, CHAT, VIDEO);
        String coverName = String.format("%s%s%s", FileUtils.generateFilenameWithUUIDOnlyWithoutSuffix(file.getOriginalFilename()), DOT, VideoCoverConstants.IMAGE_FORMAT);
        // D:\Temp\video_cover\{uuid}.jpg
        String filePath = String.format("%s%s%s", DEFAULT_DIR_PATH, BACKSLASH, coverName);
        File coverFile = new File(filePath);
        if (!coverFile.getParentFile().exists()) {
            coverFile.getParentFile().mkdirs();
        }
        VideoCoverUtils.fetchFrame(file, coverFile);
        // chat/{userId}/cover/{uuid}.jpg
        coverName = String.format("%s%s%s%s%s%s%s", CHAT, SLASH, userId, SLASH, COVER, SLASH, coverName);
        String VideoCoverURL = "";
        try {
            VideoCoverURL = MinIOUtils.uploadFile(minIOConfig.getBucketName(), coverName, new FileInputStream(coverFile), true);
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        VideoMsgVO videoMsgVO = new VideoMsgVO();
        videoMsgVO.setVideoPath(VideoURL);
        videoMsgVO.setCover(VideoCoverURL);
        return GraceJSONResult.ok(videoMsgVO);
    }

    @PostMapping("uploadChatVoice")
    public GraceJSONResult uploadChatVoice(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        return GraceJSONResult.ok(uploadFiles(file, userId, CHAT, VOICE));
    }

    private String uploadFiles(MultipartFile file, String userId, String kind, String type) {
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(filename)) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        filename = FileUtils.generateFilenameWithUUIDOnly(filename);
        // {kind}/{userId}/{type}/{uuid}.png
        filename = String.format("%s%s%s%s%s%s%s", kind, SLASH, userId, SLASH, type, SLASH, filename);
        String fileURL = "";
        try {
            fileURL = MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream(), true);
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        return fileURL;
    }
}