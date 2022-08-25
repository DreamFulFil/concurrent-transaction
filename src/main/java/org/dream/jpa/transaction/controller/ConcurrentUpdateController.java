package org.dream.jpa.transaction.controller;

import org.dream.jpa.transaction.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "concurrent")
public class ConcurrentUpdateController {

    private PostService postService;

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    /**
     * 目標: 提供一個方法更新某個 ID 下的 POST 其文章內容
     * 
     * @param postId     要更新的 POST 識別欄位值
     * @param newContent 要更新的文章內容
     */
    @PostMapping(value = "/postId/{postId}/newContent/{newContent}")
    public void concurrentUpdate(@PathVariable Integer postId, @PathVariable String newContent) {
        postService.findPostAndUpdateContent(postId, newContent);
    }

}
