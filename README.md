# UNIVERSITY
![Header Image](src/main/resources/static/images/university.gif)

---

## Project purpose

The project includes full functionality for find and edit the timetable of students and teachers at the university.
The following entities are used: Faculty, Group, Student, Teacher, Subject, Classroom, Lecture, Schedule.

---

## Project structure

The project uses the **MVC architectural pattern**. Project structure is the following:

- **Model layer** contains entities classes
- **DAO layer**, contains basic CRUD-operations
- **Service layer**, contains business logic of the application
- **Controller layer**, implements client-server communication logic
- **Spring configuration classes**

---
## Launch guide

To run this project you will need to install:

- [JDK 11 or higher](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Apache Tomcat](https://tomcat.apache.org/download-80.cgi)
- [PostgreSQL](https://www.postgresql.org/download/)

Here are the steps for you to follow:

- Add this project to your IDE as **Maven project**.
- If necessary, configure Java SDK 11 in Project Structure settings.
- Add new Tomcat Server configuration and select **war-exploded artifact** to deploy. Set **application context** parameter to "/".
- Execute SQL scripts in PostgreSQL from create_user_and_database.sql and create_tables.sql files from src/main/resources. 
- Run the project via Tomcat configuration.

---
## Author

[Olesia Kharchenko](https://github.com/OlessiaKharchenko)