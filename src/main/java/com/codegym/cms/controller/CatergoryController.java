package com.codegym.cms.controller;

import com.codegym.cms.model.Catergory;
import com.codegym.cms.model.Post;
import com.codegym.cms.service.PostService;
import com.codegym.cms.service.CatergoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CatergoryController {

    @Autowired
    private CatergoryService catergoryService;

    @Autowired
    private PostService postService;

    @GetMapping("/catergory")
    public ModelAndView listCatergory(){
        Iterable<Catergory> catergory = catergoryService.findAll();
        ModelAndView modelAndView = new ModelAndView("/catergory/list");
        modelAndView.addObject("catergory", catergory);
        return modelAndView;
    }

    @GetMapping("/create-catergory")
    public ModelAndView showCreateForm(){
        ModelAndView modelAndView = new ModelAndView("/catergory/create");
        modelAndView.addObject("catergory", new Catergory());
        return modelAndView;
    }

    @PostMapping("/create-catergory")
    public ModelAndView saveCatergory(@ModelAttribute("catergory") Catergory catergory){
        catergoryService.save(catergory);

        ModelAndView modelAndView = new ModelAndView("/catergory/create");
        modelAndView.addObject("catergory", new Catergory());
        modelAndView.addObject("message", "New catergory created successfully");
        return modelAndView;
    }

    @GetMapping("/edit-catergory/{id}")
    public ModelAndView showEditForm(@PathVariable Long id){
        Catergory catergory = catergoryService.findById(id);
        if(catergory != null) {
            ModelAndView modelAndView = new ModelAndView("/catergory/edit");
            modelAndView.addObject("catergory", catergory);
            return modelAndView;

        }else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/edit-catergory")
    public ModelAndView updateCatergory(@ModelAttribute("catergory") Catergory catergory){
        catergoryService.save(catergory);
        ModelAndView modelAndView = new ModelAndView("/catergory/edit");
        modelAndView.addObject("catergory", catergory);
        modelAndView.addObject("message", "Catergory updated successfully");
        return modelAndView;
    }

    @GetMapping("/delete-catergory/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id){
        Catergory catergory = catergoryService.findById(id);
        if(catergory != null) {
            ModelAndView modelAndView = new ModelAndView("/catergory/delete");
            modelAndView.addObject("catergory", catergory);
            return modelAndView;

        }else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/delete-catergory")
    public String deleteCatergory(@ModelAttribute("catergory") Catergory catergory){
        catergoryService.remove(catergory.getId());
        return "redirect:catergories";
    }

    @GetMapping("/view-catergory/{id}")
    public ModelAndView viewCatergory(@PathVariable("id") Long id){
        Catergory catergory = catergoryService.findById(id);
        if(catergory == null){
            return new ModelAndView("/error.404");
        }

        Iterable<Post> posts = postService.findAllByCatergory(catergory);

        ModelAndView modelAndView = new ModelAndView("/catergory/view");
        modelAndView.addObject("catergory", catergory);
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }
}
