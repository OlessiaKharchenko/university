package com.kharchenko.university.controller;

import com.kharchenko.university.exception.*;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String getAllStudents(Model model) throws EntityNotFoundException {
        model.addAttribute("students", studentService.getAll());
        return "student/students";
    }

    @GetMapping("/{id}")
    public String getStudentById(@PathVariable int id, Model model) throws EntityNotFoundException {
        model.addAttribute("student", studentService.getById(id));
        return "student/student";
    }

    // ДОДАЄМО НОВИЙ КЛАС
    @PostMapping("/add")
    public String addStudent(@ModelAttribute("student") Student student) throws InvalidEntityFieldException,
            EntityNotFoundException, InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        studentService.add(student);
        return "redirect:/students";
    }

    // ВИДАЛЕННЯ КЛАСУ
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable(value = "id") Integer id) throws EntityHasReferenceException,
            EntityNotFoundException {
        studentService.deleteById(id);
        return "redirect:/students";
    }

}
