package com.codegym.cms.controller;

import com.codegym.cms.model.Post;
import com.codegym.cms.model.Catergory;
import com.codegym.cms.service.PostService;
import com.codegym.cms.service.CatergoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CatergoryService catergoryService;

    @ModelAttribute("catergory")
    public Iterable<Catergory> catergory(){
        return catergoryService.findAll();
    }

    private String getPrincipal(){
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    @GetMapping("/admin")
    public ModelAndView listPost(@RequestParam("s") Optional<String> s, Pageable pageable){
        Page<Post> posts;
        if(s.isPresent()){
            posts = postService.findAllByTitlePostContaining(s.get(), pageable);
        } else {
            posts = postService.findAll(pageable);
        }
        ModelAndView modelAndView = new ModelAndView("/admin/post/list");
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("user", getPrincipal());
        return modelAndView;
    }

    @GetMapping("/admin/create-post")
    public ModelAndView showCreateForm(){
        ModelAndView modelAndView = new ModelAndView("/admin/post/create");
        modelAndView.addObject("post", new Post());
        return modelAndView;
    }

    @PostMapping("/admin/create-post")
    public ModelAndView savePost(@ModelAttribute("post") Post post){
        postService.save(post);
        ModelAndView modelAndView = new ModelAndView("/admin/post/create");
        modelAndView.addObject("post", new Post());
        modelAndView.addObject("message", "New post created successfully");
        return modelAndView;
    }

    @GetMapping("/admin/edit-post/{id}")
    public ModelAndView showEditForm(@PathVariable Long id){
        Post post = postService.findById(id);
        if(post != null) {
            ModelAndView modelAndView = new ModelAndView("/admin/post/edit");
            modelAndView.addObject("post", post);
            return modelAndView;

        }else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/admin/edit-post")
    public ModelAndView updatePost(@ModelAttribute("post") Post post){
        postService.save(post);
        ModelAndView modelAndView = new ModelAndView("/admin/post/edit");
        modelAndView.addObject("post", post);
        modelAndView.addObject("message", "Post updated successfully");
        return modelAndView;
    }

    @GetMapping("/admin/delete-post/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id){
        Post post = postService.findById(id);
        if(post != null) {
            ModelAndView modelAndView = new ModelAndView("/admin/post/delete");
            modelAndView.addObject("post", post);
            return modelAndView;

        }else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/admin/delete-post")
    public String deletePost(@ModelAttribute("post") Post post){
        postService.remove(post.getId());
        return "redirect:posts";
    }
}
