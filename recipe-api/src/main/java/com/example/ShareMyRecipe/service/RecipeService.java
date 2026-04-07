package com.example.ShareMyRecipe.service;

import com.example.ShareMyRecipe.dto.PageResponse;
import com.example.ShareMyRecipe.dto.RecipeDTO;
import com.example.ShareMyRecipe.entity.Follow;
import com.example.ShareMyRecipe.entity.Recipe;
import com.example.ShareMyRecipe.entity.User;
import com.example.ShareMyRecipe.enums.Status;
import com.example.ShareMyRecipe.exception.BadRequestException;
import com.example.ShareMyRecipe.exception.NotFoundException;
import com.example.ShareMyRecipe.exception.UnauthorizedException;
import com.example.ShareMyRecipe.repository.FollowRepository;
import com.example.ShareMyRecipe.repository.RecipeRepository;
import com.example.ShareMyRecipe.repository.UserRepository;
import com.example.ShareMyRecipe.specification.RecipeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository _recipeRepository;

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private FollowRepository _followRepository;

    @Autowired
    private RecipeProducer _recipeProducer;

    public String createRecipe(RecipeDTO recipeDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User chef = _userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        recipeDTO.setChefId(chef.getId());

        _recipeProducer.sendRecipe(recipeDTO);

        return "Recipe is being processed";
    }

    public Recipe publishRecipe(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = _userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Recipe recipe = _recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        if (!recipe.getChef().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only publish your own recipes");
        }

        if (recipe.getStatus() == Status.PUBLISHED) {
            throw new BadRequestException("Already published");
        }

        recipe.setStatus(Status.PUBLISHED);
        recipe.setPublishedAt(LocalDateTime.now());

        return _recipeRepository.save(recipe);
    }

    public PageResponse<Recipe> getRecipes(String q,
                                           LocalDateTime publishedFrom,
                                           LocalDateTime publishedTo,
                                           Long chefId,
                                           int page,
                                           int size) {
        size = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Specification<Recipe> recipeSpecification = RecipeSpecification.getRecipes(q,publishedFrom, publishedTo, chefId);
        Page<Recipe> recipePage = _recipeRepository.findAll(recipeSpecification, pageable);

        return new PageResponse<>(
                recipePage.getContent(),
                recipePage.getNumber(),
                recipePage.getSize(),
                recipePage.getTotalElements(),
                recipePage.getTotalPages()
        );
    }

    public PageResponse<Recipe> getFeed(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = _userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // fetches all following records by a follower.
        List<Follow> follows = _followRepository.findByFollower(user);

        List<User> followingUsers = follows.stream()
                .map(Follow::getFollowing)
                .toList();

        if (followingUsers.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, 0, 0);
        }

        size = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<Recipe> recipePage = _recipeRepository.findByChefInAndStatus(followingUsers,Status.PUBLISHED, pageable);
        return new PageResponse<>(
                recipePage.getContent(),
                recipePage.getNumber(),
                recipePage.getSize(),
                recipePage.getTotalElements(),
                recipePage.getTotalPages()
        );
    }
}
