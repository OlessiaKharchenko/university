package com.kharchenko.university.controller;

import com.kharchenko.university.exception.*;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public String getAllTeachers(Model model) throws EntityNotFoundException {
        model.addAttribute("teachers", teacherService.getAll());
        return "teacher/teachers";
    }

    @GetMapping("/{id}")
    public String getTeacherById(@PathVariable int id, Model model) throws EntityNotFoundException {
        model.addAttribute("teacher", teacherService.getById(id));
        return "teacher/teachers";
    }

    // ДОДАЄМО НОВИЙ
    @PostMapping("/add")
    public String addTeacher(@ModelAttribute("teacher") Teacher teacher) throws InvalidEntityFieldException,
            EntityNotFoundException, InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        teacherService.add(teacher);
        return "redirect:/teachers";
    }

    // ВИДАЛЕННЯ
    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable(value = "id") Integer id) throws EntityHasReferenceException,
            EntityNotFoundException {
        teacherService.deleteById(id);
        return "redirect:/teachers";
    }
}
