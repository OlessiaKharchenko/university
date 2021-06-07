package com.kharchenko.university.controller;

import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.dto.StudentDto;
import com.kharchenko.university.service.GroupService;
import com.kharchenko.university.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/students")
public class StudentController {

    private StudentService studentService;
    private GroupService groupService;

    public StudentController(StudentService studentService, GroupService groupService) {
        this.studentService = studentService;
        this.groupService = groupService;
    }

    @GetMapping
    public String getAllStudents(Model model) {
        model.addAttribute("students", studentService.getAll());
        model.addAttribute("groups", groupService.getAll());
        return "student/students";
    }

    @GetMapping("/{id}")
    public String getStudentById(@PathVariable int id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        return "student/student";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute("student") StudentDto studentDto) {
        Student student = new Student(studentDto.getId(), studentDto.getFirstName(), studentDto.getLastName(),
                groupService.getById(studentDto.getGroupId()));
        studentService.add(student);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable(value = "id") Integer id) {
        studentService.deleteById(id);
        return "redirect:/students";
    }
}
