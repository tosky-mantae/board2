package com.tosky.board2.service;

import com.tosky.board2.Vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.regex.Pattern.matches;

@RequiredArgsConstructor
@Component
public class FormAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();

        UserVo user = userService.loadUserByUsername(userId);
        // 유저 없는경우
        if (null == user ) {
            return null;
        }
        // 비번 불일치
        if (!matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
//밑에구문이 구너한 부여임
        grantedAuthorityList.add(new SimpleGrantedAuthority(user.getUsername()));

        // 로그인 성공시 로그인 사용자 정보 반환
        return new UsernamePasswordAuthenticationToken(user, null, null);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}