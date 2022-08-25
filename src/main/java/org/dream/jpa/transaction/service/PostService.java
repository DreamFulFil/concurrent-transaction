package org.dream.jpa.transaction.service;

import java.util.Optional;

import org.dream.jpa.transaction.model.Post;
import org.dream.jpa.transaction.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostService {

    private PostRepository postRepository;
    
    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @Transactional
    public Post save(Post post) {
        return this.postRepository.save(post);
    }

    @Transactional
    public void findPostAndUpdateContent(Integer postId, String newContent) {
        Optional<Post> postOptional = this.postRepository.findById(postId);
        if(postOptional.isPresent()) {
            // 這段邏輯假設取出的資料內容是存成字串的 int
            Post post = postOptional.get();
            String oldContent = post.getContent();
            int oldValue = Integer.parseInt(oldContent);
            int newValue = oldValue + 1;
            log.info("舊值: {}, 新值: {}", oldValue, newValue);
            post.setContent(Integer.toString(newValue));
        }
    }

}
