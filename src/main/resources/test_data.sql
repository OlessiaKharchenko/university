INSERT INTO faculties (faculty_name)
VALUES ('Programming');
INSERT INTO faculties (faculty_name)
VALUES ('Management');

INSERT INTO classrooms (building_number, room_number, faculty_id)
VALUES (1, 100, 1);
INSERT INTO classrooms (building_number, room_number, faculty_id)
VALUES (2, 200, 1);
INSERT INTO classrooms (building_number, room_number, faculty_id)
VALUES (3, 300, 1);

INSERT INTO subjects (subject_name, subject_description)
VALUES ('Java', 'Learn Java');
INSERT INTO subjects (subject_name, subject_description)
VALUES ('Sql', 'Learn Sql');
INSERT INTO subjects (subject_name, subject_description)
VALUES ('Spring', 'Learn Spring');
INSERT INTO subjects (subject_name, subject_description)
VALUES ('Hibernate', 'Learn Hibernate');
INSERT INTO subjects (subject_name, subject_description)
VALUES ('Junit', 'Learn Junit');

INSERT INTO teachers (first_name, last_name)
VALUES ('Bruce', 'Eckel');
INSERT INTO teachers (first_name, last_name)
VALUES ('Robert', 'Martin');
INSERT INTO teachers (first_name, last_name)
VALUES ('James', 'Gosling');

INSERT INTO groups (group_name, faculty_id)
VALUES ('AA-111', 1);
INSERT INTO groups (group_name, faculty_id)
VALUES ('BB-222', 1);
INSERT INTO groups (group_name, faculty_id)
VALUES ('CC-333', 1);
INSERT INTO groups (group_name, faculty_id)
VALUES ('DD-444', 1);

INSERT INTO students (first_name, last_name, group_id)
VALUES ('Ivan', 'Ivanov', 1);
INSERT INTO students (first_name, last_name, group_id)
VALUES ('Petr', 'Petrov', 1);
INSERT INTO students (first_name, last_name, group_id)
VALUES ('Anton', 'Antonov', 2);
INSERT INTO students (first_name, last_name, group_id)
VALUES ('Olha', 'Olhova', 2);
INSERT INTO students (first_name, last_name, group_id)
VALUES ('Mariia', 'Mariyk', 1);
INSERT INTO students (first_name, last_name, group_id)
VALUES ('Vasyl', 'Valyliev', 2);

INSERT INTO teachers_subjects (subject_id, teacher_id)
VALUES (1, 1);
INSERT INTO teachers_subjects (subject_id, teacher_id)
VALUES (2, 2);
INSERT INTO teachers_subjects (subject_id, teacher_id)
VALUES (3, 1);
INSERT INTO teachers_subjects (subject_id, teacher_id)
VALUES (4, 2);

INSERT INTO groups_subjects (subject_id, group_id)
VALUES (1, 1);
INSERT INTO groups_subjects (subject_id, group_id)
VALUES (2, 1);
INSERT INTO groups_subjects (subject_id, group_id)
VALUES (3, 2);
INSERT INTO groups_subjects (subject_id, group_id)
VALUES (4, 2);

INSERT INTO schedules (date, faculty_id)
VALUES ('2021-05-24', 1);
INSERT INTO schedules (date, faculty_id)
VALUES ('2021-05-25', 1);

INSERT INTO lectures (subject_id, teacher_id, classroom_id, start_time, end_time)
VALUES (1, 1, 1, '09:00', '11:00');
INSERT INTO lectures (subject_id, teacher_id, classroom_id, start_time, end_time)
VALUES (2, 2, 2, '12:00', '14:00');
INSERT INTO lectures (subject_id, teacher_id, classroom_id, start_time, end_time)
VALUES (1, 1, 2, '14:00', '16:00');

INSERT INTO groups_lectures (group_id, lecture_id)
VALUES (1, 1);
INSERT INTO groups_lectures (group_id, lecture_id)
VALUES (2, 1);
INSERT INTO groups_lectures (group_id, lecture_id)
VALUES (2, 2);
INSERT INTO groups_lectures (group_id, lecture_id)
VALUES (3, 2);
INSERT INTO groups_lectures (group_id, lecture_id)
VALUES (3, 3);

INSERT INTO schedules_lectures (schedule_id, lecture_id)
VALUES (1, 1);
INSERT INTO schedules_lectures (schedule_id, lecture_id)
VALUES (1, 2);
INSERT INTO schedules_lectures (schedule_id, lecture_id)
VALUES (2, 1);