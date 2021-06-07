package com.kharchenko.university.controller;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.service.FacultyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/faculties")
public class FacultyController {

    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public String getAllFaculties(Model model) {
        model.addAttribute("faculties", facultyService.getAll());
        return "faculty/faculties";
    }

    @GetMapping("/{id}")
    public String getFacultyById(@PathVariable int id, Model model) {
        model.addAttribute("faculty", facultyService.getById(id));
        return "faculty/faculty";
    }

    @PostMapping("/add")
    public String addFaculty(@ModelAttribute("classroom") Faculty faculty) {
        facultyService.add(faculty);
        return "redirect:/faculties";
    }

    @GetMapping("/delete/{id}")
    public String deleteFaculty(@PathVariable(value = "id") Integer id) {
        facultyService.deleteById(id);
        return "redirect:/faculties";
    }
}
