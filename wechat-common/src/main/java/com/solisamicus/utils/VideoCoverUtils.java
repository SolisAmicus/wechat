package com.solisamicus.utils;

import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.solisamicus.constants.VideoCoverConstants.*;

@Slf4j
public class VideoCoverUtils {
    public static void fetchFrame(String videoFilePath, String frameFilePath) {
        File videoFile = new File(videoFilePath);
        File frameFile = new File(frameFilePath);
        getFirstFrame(videoFile, frameFile);
    }

    public static void fetchFrame(MultipartFile videoFile, File targetFile) {
        File tempFile = new File(videoFile.getName());
        try {
            InputStream fileInputStream = videoFile.getInputStream();
            org.apache.commons.io.FileUtils.copyInputStreamToFile(fileInputStream, tempFile);
            getFirstFrame(tempFile, targetFile);
        } catch (IOException e) {
            log.error("处理视频文件时发生异常：", e);
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public static void getFirstFrame(File videoFile, File targetFile) {
        try {
            // 从视频文件中提取指定帧
            Picture picture = FrameGrab.getFrameFromFile(videoFile, THUMB_FRAME);
            // 将提取的帧转换为 BufferedImage
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            // 将 BufferedImage 保存为目标图片文件
            ImageIO.write(bufferedImage, IMAGE_FORMAT, targetFile);
            log.debug("成功截取帧并保存为图片：{}", targetFile.getAbsolutePath());
        } catch (IOException | JCodecException e) {
            log.error("获取第一帧缩略图失败：", e);
        }
    }
}
