package kz.kbtu.sxodimkbtu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kz.kbtu.sxodimkbtu.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    private Long eventID;
    private String name;
    private Date date;
    private String address;
    private String description;
    private EventType eventType;
    private String organizerId;
    private String departmentId;
    private List<Registration> registrations;
}
