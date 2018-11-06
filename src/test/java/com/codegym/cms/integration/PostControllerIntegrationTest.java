package com.codegym.cms.integration;

import com.codegym.cms.model.Post;
import com.codegym.cms.model.Province;
import com.codegym.cms.repository.PostRepository;
import com.codegym.cms.repository.ProvinceRepository;
import static org.hamcrest.core.StringContains.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebAppConfiguration
@SpringJUnitJupiterConfig(classes = ApplicationIntegrationTestConfig.class)
@Transactional
class PostControllerIntegrationTest {

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
    private static Long provinceId;
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
        province.setId(provinceId);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    private MockMvc mockMvc;

    @Test
    void listCustomers() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        postRepository.save(post);

        mockMvc.perform(get(URL_CUSTOMER_LIST))
                .andExpect(view().name(VIEW_CUSTOMER_LIST))
                .andExpect(model().attributeExists("posts"))
                .andExpect(result -> {
                    Page<Post> customersPage = (Page<Post>) result.getModelAndView().getModel().get("posts");
                    assertEquals(1, customersPage.getTotalElements());
                })
                .andExpect(content().string(containsString("<td>" + post.getTitlePost() + "</td>")))
                .andExpect(content().string(containsString("<td>" + province.getName() + "</td>")));
    }

    @Test
    void listCustomers_Empty() throws Exception {
        mockMvc.perform(get(URL_CUSTOMER_LIST))
                .andExpect(view().name(VIEW_CUSTOMER_LIST))
                .andExpect(model().attributeExists("posts"))
                .andExpect(result -> {
                    Page<Post> customersPage = (Page<Post>) result.getModelAndView().getModel().get("posts");
                    assertEquals(0, customersPage.getTotalElements());
                });
    }


    @Test
    void listCustomersSearch_Found() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        Post savedPost = postRepository.save(post);

        String s = "a";

        mockMvc.perform(get(URL_CUSTOMER_LIST)
                    .param("s", s))
                .andExpect(view().name(VIEW_CUSTOMER_LIST))
                .andExpect(model().attributeExists("posts"))
                .andExpect(result -> {
                    Page<Post> customersPage = (Page<Post>) result.getModelAndView().getModel().get("posts");
                    assertEquals(1, customersPage.getNumberOfElements());
                })
                .andExpect(content().string(containsString("<td>" + post.getTitlePost() + "</td>")))
                .andExpect(content().string(containsString("<td>" + province.getName() + "</td>")));
    }

    @Test
    void listCustomersSearch_NotFound() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        postRepository.save(post);

        String s = "Not existed firstname";

        mockMvc.perform(get(URL_CUSTOMER_LIST)
                .param("s", s))
                .andExpect(view().name(VIEW_CUSTOMER_LIST))
                .andExpect(model().attributeExists("posts"))
                .andExpect(result -> {
                    Page<Post> customersPage = (Page<Post>) result.getModelAndView().getModel().get("posts");
                    assertEquals(0, customersPage.getTotalElements());
                });
    }

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get(URL_CREATE_CUSTOMER))
                .andExpect(view().name(VIEW_CREATE_CUSTOMER))
                .andExpect(model().attributeExists("post"))
                .andExpect(result -> {
                    Post post = (Post) result.getModelAndView().getModel().get("post");
                    assertEquals(null, post.getId());
                    assertEquals(null, post.getTitlePost());
                    assertEquals(null, post.getLastName());
                });
    }

    @Test
    void saveCustomer_Success() throws Exception {
        Province savedProvince = provinceRepository.save(province);

        mockMvc.perform(post(URL_CREATE_CUSTOMER)
                .param("firstName", firstname)
                .param("lastName", lastname)
                .param("province", savedProvince.getId() + ""))
                .andExpect(view().name(VIEW_CREATE_CUSTOMER))
                .andExpect(model().attributeExists("post"))
                .andExpect(result -> {
                    Post post = (Post) result.getModelAndView().getModel().get("post");
                    assertEquals(null, post.getId());
                    assertEquals(null, post.getTitlePost());
                    assertEquals(null, post.getLastName());

                    String message = (String) result.getModelAndView().getModel().get("message");
                    assertEquals("New post created successfully", message);
                });
        Iterable<Post> cs = postRepository.findAll();
        Post c = cs.iterator().next();
        assertEquals(firstname, c.getTitlePost());
        assertEquals(lastname, c.getLastName());
        assertEquals(savedProvince.getId(), c.getProvince().getId());
    }

    @Test
    void showEditForm_Found() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        Post savedPost = postRepository.save(post);

        mockMvc.perform(get(URL_EDIT_CUSTOMER, savedPost.getId()))
                .andExpect(view().name(VIEW_EDIT_CUSTOMER))
                .andExpect(model().attribute("post", savedPost));
    }

    @Test
    void showEditForm_NotFound() throws Exception {
        mockMvc.perform(get(URL_EDIT_CUSTOMER, 100000))
                .andExpect(view().name(VIEW_ERROR_404));
    }

    @Test
    void updateCustomer_Success() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        Post savedPost = postRepository.save(post);

        String newFirstName = "New first name";
        String newLastName = "New last name";
        Province newProvince = new Province("New province");
        Province newSavedProvince = provinceRepository.save(newProvince);

        mockMvc.perform(post(URL_EDIT_PROVINCE_POST)
                .param("id", savedPost.getId() + "")
                .param("firstName", newFirstName)
                .param("lastName", newLastName)
                .param("province", newSavedProvince.getId() + ""))
                .andExpect(view().name(VIEW_EDIT_CUSTOMER))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("message"));

        Post c = postRepository.findOne(savedPost.getId());
        assertEquals(newFirstName, c.getTitlePost());
        assertEquals(newLastName, c.getLastName());
        assertEquals(newSavedProvince.getId(), c.getProvince().getId());
    }

    @Test
    void showDeleteForm_Found() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        Post savedPost = postRepository.save(post);

        mockMvc.perform(get(URL_DELETE_CUSTOMER, savedPost.getId()))
                .andExpect(view().name(VIEW_DELETE_CUSTOMER))
                .andExpect(model().attribute("post", savedPost));
    }

    @Test
    void showDeleteForm_NotFound() throws Exception {
        mockMvc.perform(get(URL_DELETE_CUSTOMER, 10000))
                .andExpect(view().name(VIEW_ERROR_404));
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        Province savedProvince = provinceRepository.save(province);
        post.setProvince(savedProvince);
        Post savedPost = postRepository.save(post);

        mockMvc.perform(post(URL_DELETE_CUSTOMER_POST)
                .param("id", savedPost.getId() + ""))
                .andExpect(view().name("redirect:posts"));

        Post c = postRepository.findOne(savedPost.getId());
        assertNull(c);
    }
}