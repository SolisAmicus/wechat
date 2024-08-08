package com.solisamicus.controller;

import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.pojo.FriendRequest;
import com.solisamicus.pojo.bo.FriendRequestBO;
import com.solisamicus.result.PagedGridResult;
import com.solisamicus.service.IFriendRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.solisamicus.constants.Properties.*;

@RestController
@RequestMapping("friendRequest")
public class FriendRequestController {
    @Autowired
    private IFriendRequestService friendRequestService;

    @PostMapping("add")
    public GraceJSONResult add(@RequestBody @Valid FriendRequestBO friendRequestBO) {
        friendRequestService.addNewFriendRequest(friendRequestBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("queryNew")
    public GraceJSONResult queryNew(@RequestParam("userId") String userId,
                                    @RequestParam(defaultValue = NEW_FRIEND_PAGE, name = "page") Integer page,
                                    @RequestParam(defaultValue = NEW_FRIEND_PAGE_SIZE, name = "pageSize") Integer pageSize) {
        return GraceJSONResult.ok(friendRequestService.queryNewFriendRequest(userId, page, pageSize));
    }

    @PostMapping("pass")
    public GraceJSONResult pass(@RequestParam("friendRequestId") String friendRequestId,
                                @RequestParam("friendRemark") String friendRemark) {
        friendRequestService.passNewFriendRequest(friendRequestId, friendRemark);
        return GraceJSONResult.ok();
    }
}
