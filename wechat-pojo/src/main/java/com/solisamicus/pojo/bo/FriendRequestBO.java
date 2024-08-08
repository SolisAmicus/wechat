package com.solisamicus.pojo.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestBO {
    @NotBlank
    private String myId;

    @NotBlank
    private String friendId;

    @NotBlank
    private String verifyMessage;

    private String friendRemark;
}
