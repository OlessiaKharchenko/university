package com.kharchenko.university.controller;

import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.dto.ClassRoomDto;
import com.kharchenko.university.service.ClassRoomService;
import com.kharchenko.university.service.FacultyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/classrooms")
public class ClassRoomController {

    private ClassRoomService classRoomService;
    private FacultyService facultyService;

    public ClassRoomController(ClassRoomService classRoomService, FacultyService facultyService) {
        this.classRoomService = classRoomService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String getAllClassRooms(Model model) {
        model.addAttribute("classrooms", classRoomService.getAll());
        model.addAttribute("faculties", facultyService.getAll());
        return "classroom/classrooms";
    }

    @GetMapping("/{id}")
    public String getClassroomById(@PathVariable int id, Model model) {
        model.addAttribute("classroom", classRoomService.getById(id));
        return "classroom/classroom";
    }

    @PostMapping("/add")
    public String addClassRoom(@ModelAttribute("classroom") ClassRoomDto classRoomDto) {
        ClassRoom classroom = new ClassRoom(classRoomDto.getId(), classRoomDto.getBuildingNumber(),
                classRoomDto.getRoomNumber(), facultyService.getById(classRoomDto.getFacultyId()));
        classRoomService.add(classroom);
        return "redirect:/classrooms";
    }

    @GetMapping("/delete/{id}")
    public String deleteClassRoom(@PathVariable(value = "id") Integer id) {
        classRoomService.deleteById(id);
        return "redirect:/classrooms";
    }
}
