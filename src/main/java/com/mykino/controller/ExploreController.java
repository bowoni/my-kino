package com.mykino.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExploreController {

    @GetMapping("/explore")
    public String explore() {
        return "explore/index";
    }
}
