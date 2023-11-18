package kz.kbtu.sxodimkbtu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    private Long id;
    private String name;
    private Date date;
    private String address;
    private String description;
    private EventType eventType;
    private String organizerId;
    private String departmentId;
}