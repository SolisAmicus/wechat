package com.solisamicus.pojo.netty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMsg {

    private String senderId;
    private String receiverId;
    private String msg;
    private Integer msgType;
    private String msgId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime chatTime;
    private Integer showMsgDateTimeFlag;

    private String videoPath;
    private Integer videoWidth;
    private Integer videoHeight;
    private Integer videoTimes;

    private String voicePath;
    private Integer speakVoiceDuration;
    private Boolean isRead;

    private Integer communicationType;

    private Boolean isReceiverOnLine;
}
