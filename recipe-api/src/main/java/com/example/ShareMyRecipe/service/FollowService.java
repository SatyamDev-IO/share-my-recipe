package com.example.ShareMyRecipe.service;

import com.example.ShareMyRecipe.entity.Follow;
import com.example.ShareMyRecipe.entity.User;
import com.example.ShareMyRecipe.enums.Role;
import com.example.ShareMyRecipe.enums.Status;
import com.example.ShareMyRecipe.exception.BadRequestException;
import com.example.ShareMyRecipe.exception.NotFoundException;
import com.example.ShareMyRecipe.exception.UnauthorizedException;
import com.example.ShareMyRecipe.repository.FollowRepository;
import com.example.ShareMyRecipe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    @Autowired
    private FollowRepository _followRepository;
    @Autowired
    private UserRepository _userRepository;

    public Follow followChef(Long chefId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User follower = _userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        User following = _userRepository.findById(chefId)
                .orElseThrow(() -> new NotFoundException("Chef not found"));

        if(follower.getId().equals(following.getId())){
            throw new BadRequestException("You cannot follow yourself");
        }

        if(!following.getRole().equals(Role.CHEF)){
            throw new BadRequestException("You can only follow Chefs");
        }

        boolean alreadyExists = _followRepository.existsByFollowerAndFollowing(follower, following);

        if (alreadyExists) {
            throw new BadRequestException("Already following this chef");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        return _followRepository.save(follow);

    }

    public String unfollowChef(Long chefId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User follower = _userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        User following = _userRepository.findById(chefId)
                .orElseThrow(() -> new NotFoundException("Chef not found"));

       Follow follow = _followRepository.findByFollowerAndFollowing(follower, following)
               .orElseThrow(() -> new NotFoundException("Follow not found"));

       _followRepository.delete(follow);

       return "Unfollowed Successfully";
    }
}
