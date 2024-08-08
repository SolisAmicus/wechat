package com.solisamicus.controller;

import com.solisamicus.enums.Black;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.service.IFriendShipService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.solisamicus.constants.Properties.HEADER_USER_ID;

@RestController
@RequestMapping("friendship")
public class FriendShipController {
    @Autowired
    private IFriendShipService friendShipService;

    @PostMapping("getFriendship")
    public GraceJSONResult getFriendShip(@RequestParam("friendId") String friendId, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendShipService.getFriendShip(myId, friendId));
    }

    @PostMapping("queryMyFriends")
    public GraceJSONResult queryMyFriends(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendShipService.queryMyFriends(myId, false));
    }

    @PostMapping("queryMyBlackList")
    public GraceJSONResult queryMyBlackList(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendShipService.queryMyFriends(myId, true));
    }

    @PostMapping("updateFriendRemark")
    public GraceJSONResult updateFriendRemark(@RequestParam("friendId") String friendId, @RequestParam("friendRemark") String friendRemark, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId) || StringUtils.isBlank(friendRemark)) {
            return GraceJSONResult.error();
        }
        String myId = request.getHeader(HEADER_USER_ID);
        friendShipService.updateFriendRemark(myId, friendId, friendRemark);
        return GraceJSONResult.ok();
    }

    @PostMapping("tobeBlack")
    public GraceJSONResult tobeBlack(@RequestParam("friendId") String friendId, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        String myId = request.getHeader(HEADER_USER_ID);
        friendShipService.updateBlackList(myId, friendId, Black.YES);
        return GraceJSONResult.ok();
    }

    @PostMapping("moveOutBlack")
    public GraceJSONResult moveOutBlack(@RequestParam("friendId") String friendId, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        String myId = request.getHeader(HEADER_USER_ID);
        friendShipService.updateBlackList(myId, friendId, Black.NO);
        return GraceJSONResult.ok();
    }

    @PostMapping("delete")
    public GraceJSONResult delete(HttpServletRequest request, String friendId) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        String myId = request.getHeader(HEADER_USER_ID);
        friendShipService.delete(myId, friendId);
        return GraceJSONResult.ok();
    }
}