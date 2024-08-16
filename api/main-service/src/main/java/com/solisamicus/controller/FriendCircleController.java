package com.solisamicus.controller;

import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.pojo.bo.FriendCircleBO;
import com.solisamicus.pojo.vo.FriendCircleVO;
import com.solisamicus.result.PagedGridResult;
import com.solisamicus.service.ICommentService;
import com.solisamicus.service.IFriendCircleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solisamicus.constants.Properties.HEADER_USER_ID;

@RestController
@RequestMapping("friendCircle")
public class FriendCircleController {
    @Autowired
    private IFriendCircleService friendCircleService;

    @Autowired
    private ICommentService commentService;

    @PostMapping("publish")
    public GraceJSONResult publish(@RequestBody FriendCircleBO friendCircleBO, HttpServletRequest request) {
        friendCircleService.publish(friendCircleBO, request.getHeader(HEADER_USER_ID));
        return GraceJSONResult.ok();
    }

    @PostMapping("queryList")
    public GraceJSONResult queryList(@RequestParam("userId") String userId,
                                     @RequestParam(defaultValue = "1", name = "page") Integer page,
                                     @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {
        PagedGridResult friendCircles = friendCircleService.queryFriendCircles(userId, page, pageSize);
        List<FriendCircleVO> friendCircleVOS = (List<FriendCircleVO>) friendCircles.getRows();
        for (FriendCircleVO friendCircleVO : friendCircleVOS) {
            String friendCircleId = friendCircleVO.getFriendCircleId();
            friendCircleVO.setLikedFriends(friendCircleService.queryLikedFriends(friendCircleId));
            friendCircleVO.setDoILike(friendCircleService.doILike(friendCircleId,userId));
            friendCircleVO.setCommentList(commentService.query(friendCircleId));
        }
        return GraceJSONResult.ok(friendCircles);
    }

    @PostMapping("like")
    public GraceJSONResult like(@RequestParam("friendCircleId") String friendCircleId,
                                HttpServletRequest request) {
        friendCircleService.like(friendCircleId, request.getHeader(HEADER_USER_ID));
        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam("friendCircleId") String friendCircleId,
                                  HttpServletRequest request) {
        friendCircleService.unlike(friendCircleId, request.getHeader(HEADER_USER_ID));
        return GraceJSONResult.ok();
    }

    @PostMapping("likedFriends")
    public GraceJSONResult likedFriends(@RequestParam("friendCircleId") String friendCircleId) {
        return GraceJSONResult.ok(friendCircleService.queryLikedFriends(friendCircleId));
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String friendCircleId,
                                  HttpServletRequest request) {

        friendCircleService.delete(friendCircleId, request.getHeader(HEADER_USER_ID));
        return GraceJSONResult.ok();
    }
}
