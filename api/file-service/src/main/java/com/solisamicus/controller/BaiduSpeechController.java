package com.solisamicus.controller;

import com.baidu.aip.speech.AipSpeech;
import com.solisamicus.config.BaiduSpeechProperties;
import com.solisamicus.grace.result.GraceJSONResult;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;
import java.util.HashMap;

@RestController
@RequestMapping("speech")
public class BaiduSpeechController {

    @Autowired
    private BaiduSpeechProperties baiduSpeechProperties;

    @PostMapping(value = "uploadVoice")
    public GraceJSONResult uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        byte[] pcmbytedata = mp3ConvertToPcm(file.getInputStream());
        HashMap<String, Object> options = new HashMap<String, Object>();
        int dev_id = 1537;
        options.put("dev_pid", dev_id);
        JSONObject jsonfrombaidu = basicByData(pcmbytedata, "pcm", options);
        JSONArray jsonArray = jsonfrombaidu.getJSONArray("result");
        String result = jsonArray.getString(0);
        return GraceJSONResult.ok(result);
    }

    public byte[] mp3ConvertToPcm(InputStream inputStream) throws Exception {
        AudioInputStream audioInputStream = getPcmAudioInputStream(inputStream);
        byte[] pcmBytes = IOUtils.toByteArray(audioInputStream);
        return pcmBytes;
    }

    private AudioInputStream getPcmAudioInputStream(InputStream inputStream) {
        AudioInputStream audioInputStream = null;
        AudioFormat targetFormat = null;
        try {
            AudioInputStream in = null;
            MpegAudioFileReader mp = new MpegAudioFileReader();
            in = mp.getAudioInputStream(inputStream);
            AudioFormat baseFormat = in.getFormat();
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioInputStream;
    }

    public JSONObject basicByData(byte[] voicedata, String fileType, HashMap<String, Object> options) {
        AipSpeech client = getClient();
        return client.asr(voicedata, fileType, 16000, options);
    }

    public AipSpeech getClient() {
        AipSpeech client = new AipSpeech(
                baiduSpeechProperties.getAppId(),
                baiduSpeechProperties.getApiKey(),
                baiduSpeechProperties.getSecretKey()
        );
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return client;
    }
}


