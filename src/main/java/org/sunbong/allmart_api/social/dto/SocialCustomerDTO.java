package org.sunbong.allmart_api.social.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
public class    SocialCustomerDTO implements UserDetails, OAuth2User {

    private Long id;
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;


    private List<String> roles;

    private Map<String, Object> props;

    @Override
    public Map<String, Object> getAttributes() {
        return props;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role)).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return name;
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

    @Override
    public String getName() {
        return this.email;
    }

    public void addAttribute(String key, Object value) {
        if (this.props == null) {
            this.props = new HashMap<>();
        }
        this.props.put(key, value);
    }
}
