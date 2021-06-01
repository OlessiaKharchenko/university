package com.kharchenko.university.controller;

import com.kharchenko.university.exception.*;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public String getAllGroups(Model model) throws EntityNotFoundException {
        model.addAttribute("groups", groupService.getAll());
        return "group/groups";
    }

    @GetMapping("/{id}")
    public String getGroupById(@PathVariable int id, Model model) throws EntityNotFoundException {
        model.addAttribute("faculty", groupService.getById(id));
        return "group/group";
    }


    // ДОДАЄМО НОВИЙ
    @PostMapping("/add")
    public String addGroup(@ModelAttribute("group") Group group) throws InvalidEntityFieldException,
            EntityNotFoundException, InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        groupService.add(group);
        return "redirect:/groups";
    }


    // ВИДАЛЕННЯ
    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable(value = "id") Integer id) throws EntityHasReferenceException,
            EntityNotFoundException {
        groupService.deleteById(id);
        return "redirect:/groups";
    }
}
