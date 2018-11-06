package com.codegym.cms.model;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String titlePost;
    private String bodyText;

    @ManyToOne
    @JoinColumn(name = "catergory_id")
    private Catergory catergory;

    public Post() {}

    public Post(String titlePost, String bodyText) {
        this.titlePost = titlePost;
        this.bodyText = bodyText;
    }

    @Override
    public String toString() {
        return String.format("Post[id=%d, titlePost='%s', bodyText='%s']", id, titlePost, bodyText);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitlePost() {
        return titlePost;
    }

    public void setTitlePost(String titlePost) {
        this.titlePost = titlePost;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public Catergory getCatergory() {
        return catergory;
    }

    public void setCatergory(Catergory catergory) {
        this.catergory = catergory;
    }
}