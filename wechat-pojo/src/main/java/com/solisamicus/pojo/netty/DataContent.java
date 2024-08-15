package com.solisamicus.pojo.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataContent {
    private ChatMsg chatMsg;
    private String chatTime;
}