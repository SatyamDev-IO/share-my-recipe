package com.example.ShareMyRecipe.entity;

import com.example.ShareMyRecipe.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isEnabled;

    public User(Long id, String username, String password, Role role, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isEnabled = isEnabled;
    }
    public User(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
