package com.codegym.cms.controller;

import com.codegym.cms.model.Post;
import com.codegym.cms.model.Province;
import com.codegym.cms.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

@SpringJUnitJupiterConfig(CustomerControllerTestConfig.class)
@WebAppConfiguration
class PostControllerTest {

    private static final String URL_CUSTOMER_LIST = "/posts";
    private static final String URL_CREATE_CUSTOMER = "/create-post";
    private static final String URL_EDIT_CUSTOMER = "/edit-post/{id}";
    private static final String URL_EDIT_PROVINCE_POST = "/edit-post";
    private static final String URL_DELETE_CUSTOMER = "/delete-post/{id}";
    private static final String URL_DELETE_CUSTOMER_POST = "/delete-post";

    private static final String VIEW_ERROR_404 = "/error.404";
    private static final String VIEW_CUSTOMER_LIST = "/post/list";
    private static final String VIEW_CREATE_CUSTOMER = "/post/create";
    private static final String VIEW_EDIT_CUSTOMER = "/post/edit";
    private static final String VIEW_DELETE_CUSTOMER = "/post/delete";

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
    private PostController postController;

    @Autowired
    PostService postService;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setCustomArgumentResolvers(pageableHandlerMethodArgumentResolver)
                .build();
    }

    @AfterEach
    void resetMoc(){
        Mockito.reset(postService);
    }

    @Test
    void listCustomers() throws Exception {
        when(postService.findAll(pageable)).thenReturn(customersPage);

        mockMvc.perform(get(URL_CUSTOMER_LIST))
                .andExpect(view().name(VIEW_CUSTOMER_LIST))
                .andExpect(model().attribute("posts", customersPage));
        verify(postService).findAll(pageable);
    }

    @Test
    void listCustomers_Search() throws Exception {
        String s = "a";
        when(postService.findAllByFirstNameContaining(s, pageable)).thenReturn(customersPage);

        mockMvc.perform(get(URL_CUSTOMER_LIST)
                    .param("s", s))
                .andExpect(view().name(VIEW_CUSTOMER_LIST))
                .andExpect(model().attribute("posts", customersPage));
        verify(postService).findAllByFirstNameContaining(s, pageable);
    }

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get(URL_CREATE_CUSTOMER))
                .andExpect(view().name(VIEW_CREATE_CUSTOMER))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void saveCustomer_Success() throws Exception {
        mockMvc.perform(post(URL_CREATE_CUSTOMER)
                .param("firstName", firstname)
                .param("lastName", lastname))
                .andExpect(view().name(VIEW_CREATE_CUSTOMER))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("message"));
        verify(postService).save(any(Post.class));
    }

    @Test
    void showEditForm_Found() throws Exception {
        when(postService.findById(id)).thenReturn(post);

        mockMvc.perform(get(URL_EDIT_CUSTOMER, id))
                .andExpect(view().name(VIEW_EDIT_CUSTOMER))
                .andExpect(model().attribute("post", post));
        verify(postService).findById(id);
    }

    @Test
    void showEditForm_NotFound() throws Exception {
        when(postService.findById(id)).thenReturn(null);

        mockMvc.perform(get(URL_EDIT_CUSTOMER, id))
                .andExpect(view().name(VIEW_ERROR_404));
        verify(postService).findById(id);
    }

    @Test
    void updateCustomer_Success() throws Exception {
        mockMvc.perform(post(URL_EDIT_PROVINCE_POST)
                .param("firstName", "new f name")
                .param("lastName", "new l name"))
                .andExpect(view().name(VIEW_EDIT_CUSTOMER))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("message"));
        verify(postService).save(any(Post.class));
    }

    @Test
    void showDeleteForm_Found() throws Exception {
        when(postService.findById(id)).thenReturn(post);

        mockMvc.perform(get(URL_DELETE_CUSTOMER, id))
                .andExpect(view().name(VIEW_DELETE_CUSTOMER))
                .andExpect(model().attribute("post", post));
        verify(postService).findById(id);
    }

    @Test
    void showDeleteForm_NotFound() throws Exception {
        when(postService.findById(id)).thenReturn(null);

        mockMvc.perform(get(URL_DELETE_CUSTOMER, id))
                .andExpect(view().name(VIEW_ERROR_404));
        verify(postService).findById(id);
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        mockMvc.perform(post(URL_DELETE_CUSTOMER_POST)
                .param("id", id + ""))
                .andExpect(view().name("redirect:posts"));
        verify(postService).remove(id);
    }
}