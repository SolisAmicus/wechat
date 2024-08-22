package com.solisamicus.pojo.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServerNode {
    private String host;
    private Integer port;
    private Integer onlineCounts = 0;
}
