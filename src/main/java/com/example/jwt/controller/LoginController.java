package com.example.jwt.controller;

import com.example.jwt.model.LoginForm;
import com.example.jwt.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ModelAndView getLogin() {
        return new ModelAndView("login", "form", new LoginForm());
    }

    @PostMapping
    public ModelAndView submitLogin(
        @ModelAttribute("form") LoginForm form,
        HttpServletResponse response
    ) {
        try {
            String token = authService.login(form.getUsername(), form.getPassword());

            Cookie cookie = new Cookie("X-Token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return new ModelAndView("redirect:/");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ModelAndView("login", HttpStatus.UNAUTHORIZED);
        }
    }

}
