package com.kharchenko.university.controller;

import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.dto.ScheduleDto;
import com.kharchenko.university.service.FacultyService;
import com.kharchenko.university.service.LectureService;
import com.kharchenko.university.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private ScheduleService scheduleService;
    private FacultyService facultyService;
    private LectureService lectureService;

    public ScheduleController(ScheduleService scheduleService, FacultyService facultyService, LectureService lectureService) {
        this.scheduleService = scheduleService;
        this.facultyService = facultyService;
        this.lectureService = lectureService;
    }

    @GetMapping
    public String getAllSchedules(Model model) {
        model.addAttribute("schedules", scheduleService.getAll());
        model.addAttribute("faculties", facultyService.getAll());
        model.addAttribute("lectures", lectureService.getAll());
        return "schedule/schedules";
    }

    @GetMapping("/{id}")
    public String getScheduleById(@PathVariable int id, Model model) {
        model.addAttribute("schedule", scheduleService.getById(id));
        return "schedule/schedules";
    }

    @PostMapping("/add")
    public String addSchedule(@ModelAttribute("schedule") ScheduleDto scheduleDto, @RequestParam(value = "scheduleLectures")
            List<Integer> scheduleLectures) {
        List<Lecture> lectures = scheduleLectures.stream()
                .map(lectureService::getById)
                .collect(Collectors.toList());
        Schedule schedule = new Schedule(scheduleDto.getId(),
                lectures,
                LocalDate.parse(scheduleDto.getDate()),
                facultyService.getById(scheduleDto.getFacultyId()));
        scheduleService.add(schedule);
        return "redirect:/schedules";
    }

    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable(value = "id") Integer id) {
        scheduleService.deleteById(id);
        return "redirect:/schedules";
    }
}
