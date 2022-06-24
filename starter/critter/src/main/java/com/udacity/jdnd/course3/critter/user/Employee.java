package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.schedule.Schedule;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.Set;

@Entity
@Getter
@Setter
public class Employee {
        @Id
        @GeneratedValue
        private long id;

        private String name;

        @ElementCollection
        private Set<EmployeeSkill> skills;

        @ElementCollection
        private Set<DayOfWeek> daysAvailable;

        @ManyToMany(mappedBy = "employees")
        private Set<Schedule> schedules;
}
