package com.solisamicus.controller;

import com.solisamicus.config.MinIOConfig;
import com.solisamicus.exceptions.GraceException;
import com.solisamicus.feign.UserInfoMicroServiceFeign;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.vo.UsersVO;
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

import static com.solisamicus.constants.Properties.FACE_DIRECTORY;
import static com.solisamicus.constants.Properties.QRCODE_DIRECTORY;
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
        UsersVO usersVO = JsonUtils.jsonToPojo(JsonUtils.objectToJson(jsonResult.getData()), UsersVO.class);
        return GraceJSONResult.ok(usersVO);
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
            String qrCodeUrl = null;
            try {
                qrCodeUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, qrCodePath, true);
            } catch (Exception e) {
                GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
            }
            return qrCodeUrl;
        }
        return null;
    }
}


