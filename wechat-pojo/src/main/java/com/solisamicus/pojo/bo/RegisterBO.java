package com.solisamicus.pojo.bo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterBO {
    @Pattern(regexp = "^1(3|4|5|7|8)\\d{9}$",message = "手机号码不符合标准")
    private String mobile;
    @Size(max = 6,min = 6,message = "验证码不符合标准")
    private String smsCode;
    private String nickname;
}
