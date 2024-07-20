package com.solisamicus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solisamicus.constants.Properties;
import com.solisamicus.enums.Sex;
import com.solisamicus.feign.FileMicroServiceFeign;
import com.solisamicus.mapper.UsersMapper;
import com.solisamicus.pojo.Users;
import com.solisamicus.service.IUsersService;
import com.solisamicus.utils.MaskUtil;
import com.solisamicus.utils.LocalDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsersServiceImpl extends Properties implements IUsersService {
    @Autowired
    private UsersMapper usersMapper;

    private static final String USER_FACE = "https://s2.loli.net/2024/07/01/m4prdh51voRDCQj.png";
    private static final String USER_FRIEND_CIRCLE_BG = "https://s2.loli.net/2024/07/17/bMTV9s8ZPKRvHW6.png";
    private static final String USER_CHAT_BG = "https://s2.loli.net/2024/07/17/j4H5PkfRi87ScKA.png";


    @Override
    public Users queryMobileIfExist(String mobile) {
        return usersMapper.selectOne(
                new QueryWrapper<Users>()
                        .eq("mobile", mobile)
        );
    }

    @Transactional
    @Override
    public Users createUsers(String mobile, String nickname) {
        Users user = new Users();
        String wechatNum = String.format("wx%s", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
        user.setWechatNum(wechatNum);
        user.setWechatNumImg(getQrCodeUrl(wechatNum));
        user.setMobile(mobile);
        if (StringUtils.isBlank(nickname)) {
            user.setNickname("用户" + MaskUtil.commonDisplay(mobile));
        } else {
            user.setNickname(nickname);
        }
        user.setRealName("");
        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE);
        user.setEmail("");
        user.setBirthday(LocalDateUtils.parseLocalDate("1980-01-01", LocalDateUtils.DATE_PATTERN));
        user.setCountry("");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setFriendCircleBg(USER_FRIEND_CIRCLE_BG);
        user.setChatBg(USER_CHAT_BG);
        user.setSignature("");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        usersMapper.insert(user);
        return user;
    }

    @Autowired
    private FileMicroServiceFeign fileMicroServiceFeign;

    private String getQrCodeUrl(String wechatNumber) {
        return fileMicroServiceFeign.generatorQrCode("temp", wechatNumber);
    }
}
