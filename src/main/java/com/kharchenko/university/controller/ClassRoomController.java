package com.kharchenko.university.controller;

import com.kharchenko.university.exception.*;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.service.ClassRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/classrooms")
public class ClassRoomController {

    private ClassRoomService classRoomService;

    public ClassRoomController(ClassRoomService classRoomService) {
        this.classRoomService = classRoomService;
    }

    @GetMapping
    public String getAllClassRooms(Model model) throws EntityNotFoundException {
        model.addAttribute("classrooms", classRoomService.getAll());
        return "classroom/classrooms";
    }

    @GetMapping("/{id}")
    public String getClassroomById(@PathVariable int id, Model model) throws EntityNotFoundException {
        model.addAttribute("classroom", classRoomService.getById(id));
        return "classroom/classroom";
    }


    // ДОДАЄМО НОВИЙ КЛАС
    @PostMapping("/add")
    public String addClassRoom(@ModelAttribute("classroom") ClassRoom classRoom) throws InvalidEntityFieldException,
            EntityNotFoundException, InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        classRoomService.add(classRoom);
        return "redirect:/classrooms";
    }


    // ВИДАЛЕННЯ КЛАСУ
    @GetMapping("/delete/{id}")
    public String deleteClassRoom(@PathVariable(value = "id") Integer id) throws EntityHasReferenceException,
            EntityNotFoundException {
        classRoomService.deleteById(id);
        return "redirect:/classrooms";
    }

}
