package com.example.ShareMyRecipe.service;

import com.example.ShareMyRecipe.dto.UserDTO;
import com.example.ShareMyRecipe.entity.User;
import com.example.ShareMyRecipe.entity.VerificationToken;
import com.example.ShareMyRecipe.enums.Role;
import com.example.ShareMyRecipe.exception.BadRequestException;
import com.example.ShareMyRecipe.exception.UnauthorizedException;
import com.example.ShareMyRecipe.repository.UserRepository;
import com.example.ShareMyRecipe.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.ShareMyRecipe.util.TokenUtil;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserRepository _userRepository;
    @Autowired
    private PasswordEncoder _passwordEncoder;
    @Autowired
    private VerificationTokenRepository _verificationTokenRepository;
    public User registerUser(UserDTO userDTO) {
        if (_userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userDTO.getRole() == Role.ADMIN) {
            throw new BadRequestException("Invalid role");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(_passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        user.setEnabled(false);
        _userRepository.save(user);
        return user;
    }


    public void saveVerificationToken(User user, String verificationToken) {
        VerificationToken token = new VerificationToken();
        token.setToken(verificationToken);
        token.setUser(user);
        token.setExpiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        _verificationTokenRepository.save(token);
    }

    public VerificationToken verifyRegistrationToken(String verificationToken) {
        VerificationToken fetchedToken = _verificationTokenRepository.findByToken(verificationToken);
        if(fetchedToken == null){
           return null;
        }
        long registeredTime = fetchedToken.getExpiryDate().getTime();
        if (System.currentTimeMillis() > registeredTime) {
            _verificationTokenRepository.delete(fetchedToken);
            return null;
        }

        return fetchedToken;

    }

    public void enableUser(VerificationToken token) {
        User fetchedUser = token.getUser();
        fetchedUser.setEnabled(true);
        _userRepository.save(fetchedUser);
        _verificationTokenRepository.delete(token);
    }

    public String loginUser(String username, String password) {
        User user = _userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!_passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if(!user.isEnabled()){
            throw new BadRequestException("Please verify your account first");
        }

        return TokenUtil.generateJwtToken(user);
    }
}
