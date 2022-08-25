package org.dream.jpa.transaction.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.dream.jpa.transaction.model.Post;
import org.springframework.data.jpa.repository.Lock;

public interface PostRepository extends BaseRepository<Post, Integer> {
    
    // 悲觀鎖
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Post> findById(Integer id);

}
