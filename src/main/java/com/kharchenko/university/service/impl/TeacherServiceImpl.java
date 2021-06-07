package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private LectureDao lectureDao;

    @Override
    public Teacher add(Teacher teacher) {
        validateTeacherFields(teacher);
        return teacherDao.add(teacher);
    }

    @Override
    public List<Teacher> getAll() {
        return teacherDao.getAll();
    }

    @Override
    public Teacher getById(Integer id) {
        return teacherDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher doesn't exist with id " + id));
    }

    @Override
    public void update(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new EntityNotFoundException("Teacher doesn't exist with id " + teacher.getId());
        }
        validateTeacherFields(teacher);
        teacherDao.update(teacher);
    }

    @Override
    public boolean deleteById(Integer id) {
        Teacher teacher = getById(id);
        if (hasLectures(teacher)) {
            throw new EntityHasReferenceException("Teacher with id " + id + " has lectures.");
        }
        if (hasSubjects(teacher)) {
            throw new EntityHasReferenceException("Teacher with id " + id + " has subjects.");
        }
        return teacherDao.deleteById(id);
    }

    @Override
    public void addAll(List<Teacher> teachers) {
        for (Teacher teacher : teachers) {
            validateTeacherFields(teacher);
        }
        teacherDao.addAll(teachers);
    }

    @Override
    public List<Teacher> getBySubject(Subject subject) {
        return teacherDao.getBySubject(subject);
    }

    private void validateTeacherFields(Teacher teacher) {
        if (teacher.getFirstName() == null || teacher.getFirstName().isEmpty()) {
            throw new InvalidEntityFieldException("Teacher's first name can't be empty or null");
        }
        if (teacher.getLastName() == null || teacher.getLastName().isEmpty()) {
            throw new InvalidEntityFieldException("Teacher's last name can't be empty or null");
        }
    }

    private boolean hasSubjects(Teacher teacher) {
        return !teacher.getSubjects().isEmpty();
    }

    private boolean hasLectures(Teacher teacher) {
        return !lectureDao.getTeacherLectures(teacher).isEmpty();
    }
}