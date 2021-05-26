package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleMapper implements RowMapper<Schedule> {

    @Override
    public Schedule mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("schedule_id");
        LocalDate date = resultSet.getObject("date", LocalDate.class);
        Integer facultyId = resultSet.getInt("faculty_id");
        String facultyName = resultSet.getString("faculty_name");
        List<Lecture> lectures = new ArrayList<>();
        do {
            Lecture lecture = new Lecture();
            lecture.setId(resultSet.getInt("lecture_id"));
            lectures.add(lecture);
        } while (resultSet.next() && id == resultSet.getInt("schedule_id"));
        resultSet.previous();
        Faculty faculty = new Faculty(facultyId, facultyName);
        return new Schedule(id, lectures, date, faculty);
    }
}