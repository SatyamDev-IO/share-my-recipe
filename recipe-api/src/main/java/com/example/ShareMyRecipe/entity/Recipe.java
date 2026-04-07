package com.example.ShareMyRecipe.entity;

import com.example.ShareMyRecipe.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String summary;
    private String ingredients;
    private String steps;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "chef_id")
    private User chef;

    private LocalDateTime publishedAt;

    public Recipe(Long id, String title, String summary, String ingredients, String steps, Status status, User chef, LocalDateTime publishedAt ) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.ingredients = ingredients;
        this.steps = steps;
        this.status = status;
        this.chef = chef;
        this.publishedAt = publishedAt;
    }
    public Recipe(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getChef() {
        return chef;
    }

    public void setChefId(User chef) {
        this.chef = chef;
    }

    public void setChef(User chef) {
        this.chef = chef;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
