package kr.co.nice.nicein.auth.entity;


import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.security.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long memberId;

    private String username; // 로그인 id (이메일형식)
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority role;

    private String name;
    private String description;
    private String refreshToken; // 갱신 토큰 내용


    @OneToOne
    @JoinColumn(name="user_id")
    private Employee employee; // 조직도 테이블의 fk


    private String authOption;
    private String authRw;
    private String targetCompanyId;

    private String otpYn;
    private String otpKey;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> lists = Stream.of(Authority.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        return lists.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
}
