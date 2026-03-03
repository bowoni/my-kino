package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    @GetMapping
    public String mypage(@AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "mypage/profile";
    }
}
