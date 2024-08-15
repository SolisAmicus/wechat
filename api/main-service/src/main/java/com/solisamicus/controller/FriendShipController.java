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
        return GraceJSONResult.ok(friendShipService.getFriendShip(request.getHeader(HEADER_USER_ID), friendId));
    }

    @PostMapping("queryMyFriends")
    public GraceJSONResult queryMyFriends(HttpServletRequest request) {
        return GraceJSONResult.ok(friendShipService.queryMyFriends(request.getHeader(HEADER_USER_ID), false));
    }

    @PostMapping("queryMyBlackList")
    public GraceJSONResult queryMyBlackList(HttpServletRequest request) {
        return GraceJSONResult.ok(friendShipService.queryMyFriends(request.getHeader(HEADER_USER_ID), true));
    }

    @PostMapping("updateFriendRemark")
    public GraceJSONResult updateFriendRemark(@RequestParam("friendId") String friendId, @RequestParam("friendRemark") String friendRemark, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId) || StringUtils.isBlank(friendRemark)) {
            return GraceJSONResult.error();
        }
        friendShipService.updateFriendRemark(request.getHeader(HEADER_USER_ID), friendId, friendRemark);
        return GraceJSONResult.ok();
    }

    @PostMapping("tobeBlack")
    public GraceJSONResult tobeBlack(@RequestParam("friendId") String friendId, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        friendShipService.updateBlackList(request.getHeader(HEADER_USER_ID), friendId, Black.YES);
        return GraceJSONResult.ok();
    }

    @PostMapping("moveOutBlack")
    public GraceJSONResult moveOutBlack(@RequestParam("friendId") String friendId, HttpServletRequest request) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        friendShipService.updateBlackList(request.getHeader(HEADER_USER_ID), friendId, Black.NO);
        return GraceJSONResult.ok();
    }

    @PostMapping("delete")
    public GraceJSONResult delete(HttpServletRequest request, String friendId) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        friendShipService.delete(request.getHeader(HEADER_USER_ID), friendId);
        return GraceJSONResult.ok();
    }

    @GetMapping("isBlack")
    public GraceJSONResult isBlack(String friendId1st, String friendId2nd) {
        return GraceJSONResult.ok(friendShipService.isBlackEachOther(friendId1st, friendId2nd));
    }
}
