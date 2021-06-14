package com.kharchenko.university.controller;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.dto.LectureDto;
import com.kharchenko.university.service.LectureService;
import com.kharchenko.university.service.SubjectService;
import com.kharchenko.university.service.TeacherService;
import com.kharchenko.university.service.ClassRoomService;
import com.kharchenko.university.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lectures")
public class LectureController {

    private LectureService lectureService;
    private SubjectService subjectService;
    private TeacherService teacherService;
    private ClassRoomService classRoomService;
    private GroupService groupService;

    public LectureController(LectureService lectureService, SubjectService subjectService, TeacherService teacherService,
                             ClassRoomService classRoomService, GroupService groupService) {
        this.lectureService = lectureService;
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        this.classRoomService = classRoomService;
        this.groupService = groupService;
    }

    @GetMapping
    public String getAllLectures(Model model) {
        model.addAttribute("lectures", lectureService.getAll());
        model.addAttribute("subjects", subjectService.getAll());
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("classrooms", classRoomService.getAll());
        model.addAttribute("groups", groupService.getAll());
        return "lecture/lectures";
    }

    @GetMapping("/{id}")
    public String getLectureById(@PathVariable int id, Model model) {
        model.addAttribute("lecture", lectureService.getById(id));
        return "lecture/lectures";
    }

    @PostMapping("/add")
    public String addLecture(@ModelAttribute("lecture") LectureDto lectureDto, @RequestParam(value = "lectureGroups")
            List<Integer> lectureGroups) {
        List<Group> groups = lectureGroups.stream()
                .map(groupService::getById)
                .collect(Collectors.toList());
        Lecture lecture = new Lecture(lectureDto.getId(),
                subjectService.getById(lectureDto.getSubjectId()),
                teacherService.getById(lectureDto.getTeacherId()),
                classRoomService.getById(lectureDto.getClassRoomId()), groups,
                LocalTime.parse(lectureDto.getStartTime()),
                LocalTime.parse(lectureDto.getEndTime()));
        lectureService.add(lecture);
        return "redirect:/lectures";
    }

    @GetMapping("/delete/{id}")
    public String deleteLecture(@PathVariable(value = "id") Integer id) {
        lectureService.deleteById(id);
        return "redirect:/lectures";
    }
}
