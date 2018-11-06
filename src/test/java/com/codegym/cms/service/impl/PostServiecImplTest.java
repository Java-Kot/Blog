package com.codegym.cms.service.impl;

import com.codegym.cms.model.Post;
import com.codegym.cms.model.Province;
import com.codegym.cms.repository.PostRepository;
import com.codegym.cms.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitJupiterConfig(CustomerServiceImplTestConfig.class)
class PostServiecImplTest {

    private static Long id;
    private static String firstname = "Firstname";
    private static String lastname = "Lastname";
    private static ArrayList<Post> posts;
    private static ArrayList<Post> emptyPosts;
    private static Page<Post> customersPage;
    private static Page<Post> emptyCustomersPage;
    private static Post post;
    private static Pageable pageable;
    private static Province province;

    static {
        id = 1l;
        post = new Post(firstname, lastname);

        emptyPosts = new ArrayList<>();
        posts = new ArrayList<>();

        posts.add(post);

        emptyCustomersPage = new PageImpl<>(posts);
        customersPage = new PageImpl<>(posts);

        pageable = new PageRequest(0, 20);

        province = new Province("Hanoi");
    }

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    public void resetMocks(){
        reset(postRepository);
    }

    @Test
    void findAll_1customer() {
        when(postRepository.findAll(pageable)).thenReturn(customersPage);
        Page<Post> result = postService.findAll(pageable);

        verify(postRepository).findAll(pageable);
        assertEquals(customersPage, result);
    }

    @Test
    void findAll_0customer() {
        when(postRepository.findAll(pageable)).thenReturn(emptyCustomersPage);
        Page<Post> result = postService.findAll(pageable);

        verify(postRepository).findAll(pageable);
        assertEquals(emptyCustomersPage, result);
    }

    @Test
    void findById_Found() {
        when(postRepository.findOne(id)).thenReturn(post);
        Post result = postService.findById(id);

        verify(postRepository).findOne(id);
        assertEquals(post, result);
    }

    @Test
    void findById_NotFound() {
        when(postRepository.findOne(id)).thenReturn(null);
        Post result = postService.findById(id);

        verify(postRepository).findOne(id);
        assertNull(result);
    }

    @Test
    void save(){
        postService.save(post);
        verify(postRepository).save(post);
    }

    @Test
    void remove(){
        postService.remove(id);
        verify(postRepository).delete(id);
    }

    @Test
    void findAllByProvince() {
        when(postRepository.findAllByProvince(province)).thenReturn(posts);
        Iterable<Post> result = postService.findAllByProvince(province);

        verify(postRepository).findAllByProvince(province);
        assertEquals(posts, result);
    }

    @Test
    void findAllByFirstNameContaining_1Customer() {
        when(postRepository.findAllByFirstNameContaining(firstname, pageable)).thenReturn(customersPage);
        Page<Post> result = postService.findAllByFirstNameContaining(firstname, pageable);

        verify(postRepository).findAllByFirstNameContaining(firstname,pageable);
        assertEquals(customersPage, result);
    }

    @Test
    void findAllByFirstNameContaining_0Customer() {
        when(postRepository.findAllByFirstNameContaining(firstname, pageable)).thenReturn(emptyCustomersPage);
        Page<Post> result = postService.findAllByFirstNameContaining(firstname, pageable);

        verify(postRepository).findAllByFirstNameContaining(firstname,pageable);
        assertEquals(emptyCustomersPage, result);
    }
}