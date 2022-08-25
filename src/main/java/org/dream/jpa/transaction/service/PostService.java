package org.dream.jpa.transaction.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ReentrantLock lock = new ReentrantLock();
    
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
        try {
            /**
             * 因為 Service 是 Singleton，所以每個 Thread 會共用同一個 ReentrantLock instance
             * 這段設定的目的跟 "synchronized" 是一致的，但細節不同
             */
            boolean acquired = lock.tryLock(2000, TimeUnit.SECONDS);
            if(acquired) {
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
        catch(InterruptedException ex) {
            log.error("取得 lock 時發生錯誤", ex);
        }
        finally {
            if(lock.isLocked()) {
                lock.unlock();
            }
        }
    }

}
