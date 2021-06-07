package com.kharchenko.university.controller;

import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.service.SubjectService;
import com.kharchenko.university.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private TeacherService teacherService;
    private SubjectService subjectService;


    public TeacherController(TeacherService teacherService, SubjectService subjectService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public String getAllTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("subjects", subjectService.getAll());
        return "teacher/teachers";
    }

    @GetMapping("/{id}")
    public String getTeacherById(@PathVariable int id, Model model) {
        model.addAttribute("teacher", teacherService.getById(id));
        return "teacher/teachers";
    }

    @PostMapping("/add")
    public String addTeacher(@ModelAttribute("teacher") Teacher teacher, @RequestParam(value = "teacherSubjects")
            List<Integer> teacherSubjects) {
        teacher.setSubjects(teacherSubjects.stream()
                .map(subjectService::getById)
                .collect(Collectors.toList()));
        teacherService.add(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable(value = "id") Integer id) {
        teacherService.deleteById(id);
        return "redirect:/teachers";
    }
}
