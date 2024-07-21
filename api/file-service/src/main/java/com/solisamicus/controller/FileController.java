package com.solisamicus.controller;

import com.solisamicus.config.MinIOConfig;
import com.solisamicus.exceptions.GraceException;
import com.solisamicus.feign.UserInfoMicroServiceFeign;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.vo.UserVO;
import com.solisamicus.utils.FileUtils;
import com.solisamicus.utils.JsonUtils;
import com.solisamicus.utils.MinIOUtils;
import com.solisamicus.utils.QrCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.solisamicus.constants.Properties.*;
import static com.solisamicus.constants.Symbols.DOT;
import static com.solisamicus.constants.Symbols.SLASH;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private MinIOConfig minIOConfig;

    @Autowired
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                      @RequestParam("userId") String userId) {
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        filename = String.format("%s%s%s%s%s", FACE_DIRECTORY, SLASH, userId, SLASH, filename);
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
    public String generatorQrCode(@RequestParam("userId") String userId,
                                  @RequestParam("wechatNumber") String wechatNumber) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("wechatNumber", wechatNumber);
        String qrCodePath = QrCodeUtils.generateQRCode(JsonUtils.objectToJson(map));
        if (StringUtils.isNotBlank(qrCodePath)) {
            String uuid = UUID.randomUUID().toString();
            String filename = String.format("%s%s%s%s%s%s%s", QRCODE_DIRECTORY, SLASH, userId, SLASH, uuid, DOT, "png");
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
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("file") MultipartFile file,
                                                @RequestParam("userId") String userId) {
        String filename = file.getOriginalFilename();
        filename = FileUtils.generateFilenameWithUUIDOnly(filename);
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        filename = String.format("%s%s%s%s%s", FRIEND_CIRCLE_BG_DIRECTORY, SLASH, userId, SLASH, filename);
        String friendCircleBgURL = "";
        try {
            friendCircleBgURL = MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream(), true);
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateFriendCircleBg(userId, friendCircleBgURL);
        UserVO userVO = JsonUtils.jsonToPojo(JsonUtils.objectToJson(jsonResult.getData()), UserVO.class);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("uploadChatBg")
    public GraceJSONResult updateChatBg(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") String userId) {
        String filename = file.getOriginalFilename();
        filename = FileUtils.generateFilenameWithUUIDOnly(filename);
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        filename = String.format("%s%s%s%s%s", CHAT_BG_DIRECTORY, SLASH, userId, SLASH, filename);
        String chatBgURL = "";
        try {
            chatBgURL = MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream(), true);
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateChatBg(userId, chatBgURL);
        UserVO userVO = JsonUtils.jsonToPojo(JsonUtils.objectToJson(jsonResult.getData()), UserVO.class);
        return GraceJSONResult.ok(userVO);
    }
}


