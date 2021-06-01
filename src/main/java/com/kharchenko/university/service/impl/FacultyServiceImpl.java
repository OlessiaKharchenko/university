package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.ClassRoomDao;
import com.kharchenko.university.dao.FacultyDao;
import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyDao facultyDao;
    @Autowired
    private ClassRoomDao classRoomDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private ScheduleDao scheduleDao;


    @Override
    public Faculty add(Faculty faculty) throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        validateFaculty(faculty);
        return facultyDao.add(faculty);
    }

    @Override
    public List<Faculty> getAll() {
        return facultyDao.getAll();
    }

    @Override
    public Faculty getById(Integer id) throws EntityNotFoundException {
        return facultyDao.getById(id).orElseThrow(() -> new EntityNotFoundException("Faculty doesn't exist with id " + id));
    }

    @Override
    public void update(Faculty faculty) throws EntityIsAlreadyExistsException, EntityNotFoundException, InvalidEntityFieldException {
        if (faculty.getId() == null) {
            throw new EntityNotFoundException("Faculty doesn't exist with id " + faculty.getId());
        }
        validateFaculty(faculty);
        facultyDao.update(faculty);
    }

    @Override
    public boolean deleteById(Integer id) throws EntityNotFoundException, EntityHasReferenceException {
        Faculty faculty = getById(id);
        if (hasClassRooms(faculty)) {
            throw new EntityHasReferenceException("Faculty with id " + id + " has classrooms.");
        }
        if (hasGroups(faculty)) {
            throw new EntityHasReferenceException("Faculty with id " + id + " has groups.");
        }
        if (hasSchedules(faculty)) {
            throw new EntityHasReferenceException("Faculty with id " + id + " has schedules.");
        }
        return facultyDao.deleteById(id);
    }

    @Override
    public void addAll(List<Faculty> faculties) throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        for (Faculty faculty : faculties) {
            validateFaculty(faculty);
        }
        facultyDao.addAll(faculties);
    }

    private void validateFacultyFields(Faculty faculty) throws InvalidEntityFieldException {
        if (faculty.getName() == null || faculty.getName().isEmpty()) {
            throw new InvalidEntityFieldException("Faculty's name can't be empty or null");
        }
    }

    private void checkIfUnique(Faculty faculty) throws EntityIsAlreadyExistsException {
        if (facultyDao.getAll().stream().map(Faculty::getName)
                .anyMatch(name -> name.equals(faculty.getName()))) {
            throw new EntityIsAlreadyExistsException("Faculty with name " + faculty.getName() + " is already exists");
        }
    }

    private void validateFaculty(Faculty faculty) throws InvalidEntityFieldException, EntityIsAlreadyExistsException {
        validateFacultyFields(faculty);
        checkIfUnique(faculty);
    }

    private boolean hasSchedules(Faculty faculty) {
        return !scheduleDao.getByFaculty(faculty).isEmpty();
    }

    private boolean hasClassRooms(Faculty faculty) {
        return !classRoomDao.getByFaculty(faculty).isEmpty();
    }

    private boolean hasGroups(Faculty faculty) {
        return !groupDao.getByFaculty(faculty).isEmpty();
    }
}
