package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String senderId;
    private String receiverId;
    private Integer receiverType;
    private String msg;
    private Integer msgType;

    private LocalDateTime chatTime;
    private Integer showMsgDateTimeFlag;

    private String videoPath;
    private Integer videoWidth;
    private Integer videoHeight;
    private Integer videoTimes;

    private String voicePath;
    private Integer speakVoiceDuration;
    private Boolean isRead;
}

