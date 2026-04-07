package com.example.ShareMyRecipe.repository;

import com.example.ShareMyRecipe.entity.Follow;
import com.example.ShareMyRecipe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollower(User user);
}
