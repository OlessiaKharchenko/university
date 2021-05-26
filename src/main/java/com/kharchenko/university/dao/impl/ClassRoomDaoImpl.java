package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.ClassRoomDao;
import com.kharchenko.university.dao.mappers.ClassRoomMapper;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClassRoomDaoImpl extends AbstractDao<ClassRoom> implements ClassRoomDao {

    @Autowired
    private FacultyDaoImpl facultyDao;

    protected ClassRoomDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new ClassRoomMapper(), jdbcTemplate);
    }

    @Override
    public ClassRoom add(ClassRoom classRoom) {
        String query = "INSERT INTO classrooms (building_number, room_number, faculty_id) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"classroom_id"});
            fillRow(statement, classRoom);
            return statement;
        }, keyHolder);
        classRoom.setId(keyHolder.getKey().intValue());
        return classRoom;
    }

    @Override
    public void update(ClassRoom classRoom) {
        String query = "UPDATE classrooms SET building_number = ?, room_number = ?, faculty_id = ? WHERE classroom_id = ?;";
        jdbcTemplate.update(query, classRoom.getBuildingNumber(), classRoom.getRoomNumber(), classRoom.getFaculty().getId(),
                classRoom.getId());
    }

    @Override
    public List<ClassRoom> getByBuildingNumber(Integer number) {
        String query = "SELECT c.classroom_id, building_number, room_number, f.faculty_id, faculty_name FROM  classrooms c " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id WHERE building_number = ?;";
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(query, number), mapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassRoom> getByFaculty(Faculty faculty) {
        String query = "SELECT c.classroom_id, building_number, room_number, f.faculty_id, faculty_name FROM  classrooms c " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id WHERE f.faculty_id = ?;";
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(query, faculty.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    protected void fillRow(PreparedStatement statement, ClassRoom classRoom) throws SQLException {
        statement.setInt(1, classRoom.getBuildingNumber());
        statement.setInt(2, classRoom.getRoomNumber());
        Faculty faculty = classRoom.getFaculty();
        if (faculty.getId() == null) {
            facultyDao.add(faculty);
        }
        statement.setInt(3, faculty.getId());
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT c.classroom_id, building_number, room_number, f.faculty_id, faculty_name FROM " +
                "classrooms c LEFT JOIN faculties f ON c.faculty_id = f.faculty_id ORDER BY  c.classroom_id;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM classrooms WHERE classroom_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT c.classroom_id, building_number, room_number, f.faculty_id, faculty_name FROM " +
                "classrooms c LEFT JOIN faculties f ON c.faculty_id = f.faculty_id " +
                "WHERE classroom_id = ? ORDER BY  c.classroom_id;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO classrooms (building_number, room_number, faculty_id) VALUES (?, ?, ?);";
    }
}