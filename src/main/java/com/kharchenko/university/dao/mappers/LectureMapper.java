package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.ClassRoom;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LectureMapper implements RowMapper<Lecture> {

    @Override
    public Lecture mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("lecture_id");
        LocalTime startTime = resultSet.getObject("start_time", LocalTime.class);
        LocalTime endTime = resultSet.getObject("end_time", LocalTime.class);
        Integer classRoomId = resultSet.getInt("classroom_id");
        int buildingNumber = resultSet.getInt("building_number");
        int roomNumber = resultSet.getInt("room_number");
        Integer facultyId = resultSet.getInt("faculty_id");
        String facultyName = resultSet.getString("faculty_name");
        Integer subjectId = resultSet.getInt("subject_id");
        String subjectName = resultSet.getString("subject_name");
        String subjectDescription = resultSet.getString("subject_description");
        Teacher teacher = new Teacher();
        teacher.setId(resultSet.getInt("teacher_id"));
        teacher.setFirstName(resultSet.getString("first_name"));
        teacher.setLastName(resultSet.getString("last_name"));
        Subject subject = new Subject(subjectId, subjectName, subjectDescription);
        Faculty faculty = new Faculty(facultyId, facultyName);
        ClassRoom classRoom = new ClassRoom(classRoomId, buildingNumber, roomNumber, faculty);
        List<Group> groups = new ArrayList<>();
        do {
            Group group = new Group();
            group.setId(resultSet.getInt("group_id"));
            group.setName(resultSet.getString("group_name"));
            group.setFaculty(faculty);
            groups.add(group);
        } while (resultSet.next() && id == resultSet.getInt("lecture_id"));
        resultSet.previous();
        return new Lecture(id, subject, teacher, classRoom, groups, startTime, endTime);
    }
}