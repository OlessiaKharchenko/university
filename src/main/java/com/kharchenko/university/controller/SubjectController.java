package com.kharchenko.university.controller;

import com.kharchenko.university.exception.*;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public String getAllSubjects(Model model) throws EntityNotFoundException {
        model.addAttribute("subjects", subjectService.getAll());
        return "subject/subjects";
    }

    @GetMapping("/{id}")
    public String getSubjectById(@PathVariable int id, Model model) throws EntityNotFoundException {
        model.addAttribute("subject", subjectService.getById(id));
        return "subject/subject";
    }

    // ДОДАЄМО НОВИЙ
    @PostMapping("/add")
    public String addSubject(@ModelAttribute("subject") Subject subject) throws InvalidEntityFieldException,
            EntityNotFoundException, InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        subjectService.add(subject);
        return "redirect:/subjects";
    }

    // ВИДАЛЕННЯ
    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable(value = "id") Integer id) throws EntityHasReferenceException,
            EntityNotFoundException {
        subjectService.deleteById(id);
        return "redirect:/subjects";
    }
}
