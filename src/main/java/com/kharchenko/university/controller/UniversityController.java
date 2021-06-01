package com.kharchenko.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UniversityController {

    @GetMapping
    public String university() {
        return "university";
    }
}
