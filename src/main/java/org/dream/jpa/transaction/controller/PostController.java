package org.dream.jpa.transaction.controller;

import org.dream.jpa.transaction.model.Post;
import org.dream.jpa.transaction.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "post")
public class PostController {
    
    private PostService postService;

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/create")
    public Post createNewPost() {
        // 故意不從參數取資料建立 Post，節省前端還要組 JSON 的麻煩
        Post post = new Post();
        post.setContent("0");
        return postService.save(post);
    }

}
