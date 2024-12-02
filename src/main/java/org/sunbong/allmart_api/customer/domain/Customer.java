package org.sunbong.allmart_api.customer.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.sunbong.allmart_api.common.domain.BaseEntity;

import java.util.Collection;
import java.util.List;

@Entity
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@Table(name = "tbl_customer")
public class Customer extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerID")
    private Long customerID;

    @NotNull
    @Column(columnDefinition = "CHAR(11)", unique = true)
    private String phoneNumber;

    @Column
    private String email;

    @Column(length = 30)
    private String name;

    @Builder.Default
    private int loyaltyPoints = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerLoginType loginType;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }

    // 간편전화번호 로그인과 소셜로그인에서는 패스워드 필요없음
    @Override
    public String getPassword() {
        return "";
    }


    @Override
    public String getUsername() {
        return (loginType == CustomerLoginType.PHONE) ? phoneNumber : email;
    }
}