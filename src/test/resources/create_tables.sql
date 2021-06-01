DROP TABLE IF EXISTS classrooms CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS lectures CASCADE;
DROP TABLE IF EXISTS subjects CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS schedules CASCADE;
DROP TABLE IF EXISTS groups_lectures;
DROP TABLE IF EXISTS teachers_subjects;
DROP TABLE IF EXISTS groups_subjects;
DROP TABLE IF EXISTS schedules_lectures;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS faculties;


CREATE TABLE faculties
(
    faculty_id   SERIAL      NOT NULL PRIMARY KEY,
    faculty_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE classrooms
(
    classroom_id    SERIAL  NOT NULL PRIMARY KEY,
    building_number INTEGER NOT NULL,
    room_number     INTEGER NOT NULL,
    faculty_id      INTEGER NOT NULL REFERENCES faculties (faculty_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE (building_number, room_number)
);

CREATE TABLE subjects
(
    subject_id          SERIAL       NOT NULL PRIMARY KEY,
    subject_name        VARCHAR(50)  NOT NULL UNIQUE,
    subject_description VARCHAR(500) NOT NULL
);

CREATE TABLE teachers
(
    teacher_id SERIAL      NOT NULL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL
);

CREATE TABLE groups
(
    group_id   SERIAL      NOT NULL PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL UNIQUE,
    faculty_id INTEGER     NOT NULL REFERENCES faculties (faculty_id) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE groups_subjects
(
    subject_id INTEGER REFERENCES subjects (subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    group_id   INTEGER REFERENCES groups (group_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE (subject_id, group_id)
);

CREATE TABLE students
(
    student_id SERIAL      NOT NULL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    group_id   INTEGER     NOT NULL REFERENCES groups (group_id) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE teachers_subjects
(
    subject_id INTEGER REFERENCES subjects (subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    teacher_id INTEGER REFERENCES teachers (teacher_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE (subject_id, teacher_id)
);

CREATE TABLE schedules
(
    schedule_id SERIAL  NOT NULL PRIMARY KEY,
    date        DATE    NOT NULL UNIQUE,
    faculty_id  INTEGER NOT NULL REFERENCES faculties (faculty_id) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE lectures
(
    lecture_id   SERIAL  NOT NULL PRIMARY KEY,
    subject_id   INTEGER NOT NULL REFERENCES subjects (subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    teacher_id   INTEGER NOT NULL REFERENCES teachers (teacher_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    classroom_id INTEGER NOT NULL REFERENCES classrooms (classroom_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    start_time   TIME    NOT NULL,
    end_time     TIME    NOT NULL
);

CREATE TABLE schedules_lectures
(
    schedule_id INTEGER REFERENCES schedules (schedule_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    lecture_id  INTEGER REFERENCES lectures (lecture_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE (schedule_id, lecture_id)
);

CREATE TABLE groups_lectures
(
    group_id   INTEGER REFERENCES groups (group_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    lecture_id INTEGER REFERENCES lectures (lecture_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE (group_id, lecture_id)
);