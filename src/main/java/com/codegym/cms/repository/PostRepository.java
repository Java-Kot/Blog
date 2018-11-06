package com.codegym.cms.repository;

import com.codegym.cms.model.Catergory;
import com.codegym.cms.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Iterable<Post> findAllByCatergory(Catergory catergory);

    Page<Post> findAllByTitlePostContaining(String titlepost, Pageable pageable);
}
