package com.kharchenko.university.controller;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.dto.GroupDto;
import com.kharchenko.university.service.FacultyService;
import com.kharchenko.university.service.GroupService;
import com.kharchenko.university.service.SubjectService;
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
@RequestMapping("/groups")
public class GroupController {

    private GroupService groupService;
    private FacultyService facultyService;
    private SubjectService subjectService;

    public GroupController(GroupService groupService, FacultyService facultyService, SubjectService subjectService) {
        this.groupService = groupService;
        this.facultyService = facultyService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public String getAllGroups(Model model) {
        model.addAttribute("groups", groupService.getAll());
        model.addAttribute("faculties", facultyService.getAll());
        model.addAttribute("subjects", subjectService.getAll());
        return "group/groups";
    }

    @GetMapping("/{id}")
    public String getGroupById(@PathVariable int id, Model model) {
        model.addAttribute("faculty", groupService.getById(id));
        return "group/group";
    }

    @PostMapping("/add")
    public String addGroup(@ModelAttribute("group") GroupDto groupDto, @RequestParam(value = "groupSubjects")
            List<Integer> groupSubjects) {
        List<Subject> subjects = groupSubjects.stream()
                .map(subjectService::getById)
                .collect(Collectors.toList());
        Group group = new Group(groupDto.getId(), groupDto.getName(), subjects, facultyService.getById(groupDto.getFacultyId()));
        groupService.add(group);
        return "redirect:/groups";
    }

    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable(value = "id") Integer id) {
        groupService.deleteById(id);
        return "redirect:/groups";
    }
}
