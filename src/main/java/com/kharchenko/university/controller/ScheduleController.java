package com.kharchenko.university.controller;

import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public String getAllSchedules(Model model) {
        model.addAttribute("schedules", scheduleService.getAll());
        return "schedule/schedules";
    }

    @GetMapping("/{id}")
    public String getScheduleById(@PathVariable int id, Model model) {
        model.addAttribute("schedule", scheduleService.getById(id));
        return "schedule/schedules";
    }

    @PostMapping("/add")
    public String addSchedule(@ModelAttribute("schedule") Schedule schedule) {
        scheduleService.add(schedule);
        return "redirect:/schedule";
    }

    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable(value = "id") Integer id) {
        scheduleService.deleteById(id);
        return "redirect:/schedules";
    }
}
