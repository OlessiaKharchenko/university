package com.kharchenko.university.controller;

import com.kharchenko.university.model.Subject;
import com.kharchenko.university.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public String getAllSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAll());
        return "subject/subjects";
    }

    @GetMapping("/{id}")
    public String getSubjectById(@PathVariable int id, Model model) {
        model.addAttribute("subject", subjectService.getById(id));
        return "subject/subject";
    }

    @PostMapping("/add")
    public String addSubject(@ModelAttribute("subject") Subject subject) {
        subjectService.add(subject);
        return "redirect:/subjects";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable(value = "id") Integer id) {
        subjectService.deleteById(id);
        return "redirect:/subjects";
    }
}
