package com.elec_business.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "app_user", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "app_user_email_key", columnNames = {"email"})
})
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email_verified",nullable = false)
    private Boolean  emailVerified=false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Column(name = "profile_picture_url", length = Integer.MAX_VALUE)
    private String profilePictureUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "role_id")
    private UserRole role;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "email_verif_at")
    private Instant emailVerifiedAt;

    @Column(name = "phone_verif_at")
    private Instant phoneVerifiedAt;

    private String emailVerifCode;

    private String phoneVerifCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(id, appUser.id) && Objects.equals(username, appUser.username) && Objects.equals(email, appUser.email) && Objects.equals(password, appUser.password) && Objects.equals(phoneNumber, appUser.phoneNumber) && Objects.equals(emailVerified, appUser.emailVerified) && Objects.equals(phoneVerified, appUser.phoneVerified) && Objects.equals(profilePictureUrl, appUser.profilePictureUrl) && Objects.equals(role, appUser.role) && Objects.equals(createdAt, appUser.createdAt) && Objects.equals(emailVerifiedAt, appUser.emailVerifiedAt) && Objects.equals(phoneVerifiedAt, appUser.phoneVerifiedAt) && Objects.equals(emailVerifCode, appUser.emailVerifCode) && Objects.equals(phoneVerifCode, appUser.phoneVerifCode) && Objects.equals(refreshTokens, appUser.refreshTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, phoneNumber, emailVerified, phoneVerified, profilePictureUrl, role, createdAt, emailVerifiedAt, phoneVerifiedAt, emailVerifCode, phoneVerifCode, refreshTokens);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}