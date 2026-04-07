package com.example.ShareMyRecipe.controller;

import com.example.ShareMyRecipe.dto.PageResponse;
import com.example.ShareMyRecipe.dto.RecipeDTO;
import com.example.ShareMyRecipe.dto.UserDTO;
import com.example.ShareMyRecipe.entity.Follow;
import com.example.ShareMyRecipe.entity.Recipe;
import com.example.ShareMyRecipe.entity.User;
import com.example.ShareMyRecipe.entity.VerificationToken;
import com.example.ShareMyRecipe.repository.VerificationTokenRepository;
import com.example.ShareMyRecipe.service.FollowService;
import com.example.ShareMyRecipe.service.RecipeService;
import com.example.ShareMyRecipe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService _userService;

    @Autowired
    private RecipeService _recipeService;

    @Autowired
    private FollowService _followService;

    @PostMapping("/register")
    public User registorUser(@RequestBody UserDTO userDTO){
        User user = _userService.registerUser(userDTO);
        String verificationToken = UUID.randomUUID().toString();
        String verificationTokenUrl = "http://localhost:3080/verifyRegistrationToken?token=" + verificationToken;
        System.out.println("Please verify your registration by clicking on the following link: " + verificationTokenUrl);
        _userService.saveVerificationToken(user, verificationToken);
        return user;
    }

    @GetMapping("/verifyRegistrationToken")
    public String verifyRegistrationToken(@RequestParam("token") String verificationToken){
        VerificationToken token = _userService.verifyRegistrationToken(verificationToken);
        if (token != null) {
            _userService.enableUser(token);
            return "Token verification successful, user enabled. Please login to proceed.";
        } else {
            return "Token verification failed. Please try again.";
        }
    }
    @PostMapping("/signin")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        return _userService.loginUser(username, password);
    }

    @PreAuthorize("hasRole('CHEF')")
    @PostMapping("/recipes")
    public String createRecipe(@RequestBody RecipeDTO recipeDTO){
        return _recipeService.createRecipe(recipeDTO);
    }

    @PreAuthorize("hasRole('CHEF')")
    @PutMapping("/recipes/{id}/publish")
    public Recipe publishRecipe(@PathVariable Long id){
        return _recipeService.publishRecipe(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recipes")
    public PageResponse<Recipe> getRecipes(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishedFrom,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishedTo,
                                           @RequestParam(required = false) Long chefId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size){
        return _recipeService.getRecipes(q, publishedFrom, publishedTo, chefId, page, size);
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/follow/{chefId}")
    public Follow followChef(@PathVariable Long chefId){
        return _followService.followChef(chefId);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/follow/{chefId}")
    public String unfollowChef(@PathVariable Long chefId){ return _followService.unfollowChef(chefId);}

    @GetMapping("/feed")
    public PageResponse<Recipe> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return _recipeService.getFeed(page, size);
    }


}
