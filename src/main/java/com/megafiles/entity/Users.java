package com.megafiles.entity;

import com.megafiles.dto.UserDTO;
import com.megafiles.enums.Roles;
import com.megafiles.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    private Roles role;
    // New field to store profile picture URL
    private String profilePictureUrl="https://megashare.blob.core.windows.net/profiles/first-time-pfp.png";

    private UserStatus userStatus;
    private LocalDateTime registerDate;
    private LocalDateTime lastProfileUpdate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserDTO getUserDto(){
        UserDTO userDto = new UserDTO();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setRole(role);
        userDto.setProfilePictureUrl(profilePictureUrl);
        userDto.setStatus(userStatus);
        userDto.setRegisterDate(registerDate);
        userDto.setLastProfileUpdate(lastProfileUpdate);
        return userDto;
    }
}
