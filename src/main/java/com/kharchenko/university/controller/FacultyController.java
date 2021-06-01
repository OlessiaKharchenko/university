package com.kharchenko.university.controller;

import com.kharchenko.university.exception.*;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.service.FacultyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/faculties")
public class FacultyController {

    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public String getAllFaculties(Model model) throws EntityNotFoundException {
        model.addAttribute("faculties", facultyService.getAll());
        return "faculty/faculties";
    }

    @GetMapping("/{id}")
    public String getFacultyById(@PathVariable int id, Model model) throws EntityNotFoundException {
        model.addAttribute("faculty", facultyService.getById(id));
        return "faculty/faculty";
    }


    // ДОДАЄМО НОВИЙ
    @PostMapping("/add")
    public String addFaculty(@ModelAttribute("classroom") Faculty faculty) throws InvalidEntityFieldException,
            EntityNotFoundException, InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        facultyService.add(faculty);
        return "redirect:/faculties";
    }


    // ВИДАЛЕННЯ
    @GetMapping("/delete/{id}")
    public String deleteFaculty(@PathVariable(value = "id") Integer id) throws EntityHasReferenceException,
            EntityNotFoundException {
        facultyService.deleteById(id);
        return "redirect:/faculties";
    }

}
