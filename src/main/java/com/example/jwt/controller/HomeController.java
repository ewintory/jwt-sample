package com.example.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String getHome(Model model) {
        model.addAttribute("userId", 42L);
        model.addAttribute("username", "richard");
        return "home";
    }

}
