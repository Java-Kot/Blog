package com.codegym.cms.service;

import com.codegym.cms.model.Catergory;
import com.codegym.cms.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> findAll(Pageable pageable);

    Post findById(Long id);

    void save(Post post);

    void remove(Long id);

    Iterable<Post> findAllByCatergory(Catergory catergory);

    Page<Post> findAllByTitlePostContaining(String titlepost, Pageable pageable);
}
