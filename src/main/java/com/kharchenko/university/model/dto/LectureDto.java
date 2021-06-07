package com.kharchenko.university.model.dto;

import com.kharchenko.university.model.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureDto {
    private Integer id;
    private int subjectId;
    private int teacherId;
    private int classRoomId;
    private List<Group> groups;
    private String startTime;
    private String endTime;
}
