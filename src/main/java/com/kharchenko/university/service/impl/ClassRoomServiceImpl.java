package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.ClassRoomDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassRoomServiceImpl implements ClassRoomService {

    @Autowired
    private ClassRoomDao classRoomDao;
    @Autowired
    private LectureDao lectureDao;

    @Override
    public ClassRoom add(ClassRoom classRoom) throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        validateClassRoom(classRoom);
        return classRoomDao.add(classRoom);
    }

    @Override
    public List<ClassRoom> getAll() {
        return classRoomDao.getAll();
    }

    @Override
    public ClassRoom getById(Integer id) throws EntityNotFoundException {
        return classRoomDao.getById(id).orElseThrow(() -> new EntityNotFoundException("Classroom doesn't exist with id " + id));
    }

    @Override
    public void update(ClassRoom classRoom) throws EntityIsAlreadyExistsException, EntityNotFoundException,
            InvalidEntityFieldException {
        if (classRoom.getId() == null) {
            throw new EntityNotFoundException("Classroom doesn't exist with id " + classRoom.getId());
        }
        validateClassRoom(classRoom);
        classRoomDao.update(classRoom);
    }

    @Override
    public boolean deleteById(Integer id) throws EntityNotFoundException, EntityHasReferenceException {
        ClassRoom classRoom = getById(id);
        if (hasLectures(classRoom)) {
            throw new EntityHasReferenceException("ClassRoom with id " + id + " has lectures.");
        }
        return classRoomDao.deleteById(id);
    }

    @Override
    public void addAll(List<ClassRoom> classRooms) throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        for (ClassRoom classRoom : classRooms) {
            validateClassRoom(classRoom);
        }
        classRoomDao.addAll(classRooms);
    }

    @Override
    public List<ClassRoom> getByBuildingNumber(Integer number) {
        return classRoomDao.getByBuildingNumber(number);
    }

    @Override
    public List<ClassRoom> getByFaculty(Faculty faculty) {
        return classRoomDao.getByFaculty(faculty);
    }

    private void checkIfUnique(ClassRoom classRoom) throws EntityIsAlreadyExistsException {
        List<ClassRoom> classRooms = classRoomDao.getByBuildingNumber(classRoom.getBuildingNumber());
        if (classRooms.stream().mapToInt(ClassRoom::getRoomNumber)
                .anyMatch(roomNumber -> roomNumber == classRoom.getRoomNumber())) {
            throw new EntityIsAlreadyExistsException("Classroom with building number " + classRoom.getBuildingNumber()
                    + " and room number " + classRoom.getRoomNumber() + " is already exists");
        }
    }

    private boolean hasLectures(ClassRoom classRoom) {
        return !lectureDao.getByClassRoom(classRoom).isEmpty();
    }

    private void validateClassRoom(ClassRoom classRoom) throws InvalidEntityFieldException, EntityIsAlreadyExistsException {
        validateClassRoomFields(classRoom);
        checkIfUnique(classRoom);
    }

    private void validateClassRoomFields(ClassRoom classRoom) throws InvalidEntityFieldException {
        if (classRoom.getFaculty() == null) {
            throw new InvalidEntityFieldException("Classroom's faculty can't be null");
        }
    }
}
