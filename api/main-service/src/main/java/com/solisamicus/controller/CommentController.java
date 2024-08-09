package com.solisamicus.controller;

import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.pojo.bo.CommentBO;
import com.solisamicus.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private ICommentService commentService;

    @PostMapping("create")
    public GraceJSONResult create(@RequestBody CommentBO friendCircleBO) {
        return GraceJSONResult.ok(commentService.create(friendCircleBO));
    }

    @PostMapping("query")
    public GraceJSONResult query(@RequestParam("friendCircleId") String friendCircleId) {
        return GraceJSONResult.ok(commentService.query(friendCircleId));
    }

    @PostMapping("delete")
    public GraceJSONResult delete(@RequestParam("commentId") String commentId,
                                  @RequestParam("friendCircleId") String friendCircleId,
                                  @RequestParam("commentUserId") String commentUserId) {
        commentService.delete(commentId,friendCircleId,commentUserId);
        return GraceJSONResult.ok();
    }
}
