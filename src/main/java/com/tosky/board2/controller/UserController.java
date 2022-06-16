package com.tosky.board2.controller;

import com.tosky.board2.Vo.UserVo;
import com.tosky.board2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 폼
     * @return
     */
    @GetMapping("/login")
    public String userLogin(){
        return "ajax/login";
    }

    /**
     * 회원가입 폼
     * @return
     */
    @GetMapping("/signUp")
    public String signUpForm() {

        return "ajax/signUp";
    }

    /**
     * 로그인 실패 폼
     * @return
     */
    @GetMapping("/loginFail")
    public String accessDenied() {
        return "ajax/loginFail";
    }

    /**
     * 유저 페이지
     * @param model
     * @param authentication
     * @return
     */
    @GetMapping("/listAjaxTest")
    public String listAjaxTest(Model model, Authentication authentication, HttpServletRequest request) {
        //Authentication 객체를 통해 유저 정보를 가져올 수 있다.

        int pageNum = 1;
        if(!StringUtils.isEmpty(request.getParameter("pageNum"))){
            pageNum = Integer.parseInt(request.getParameter("pageNum"));
        }
        model.addAttribute("pageNum", pageNum);

        return "ajax/BoardListAjax";
    }

}