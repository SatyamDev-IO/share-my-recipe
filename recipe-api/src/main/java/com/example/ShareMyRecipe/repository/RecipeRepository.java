package com.example.ShareMyRecipe.repository;

import com.example.ShareMyRecipe.entity.Recipe;
import com.example.ShareMyRecipe.entity.User;
import com.example.ShareMyRecipe.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    Page<Recipe> findByStatus(Status status, Pageable pageable);

    Page<Recipe> findByChefInAndStatus(List<User> chef, Status status, Pageable pageable);
}
