package pl.rengreen.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "status")
public class ProjectStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer id;

    @Column(name = "status")
    private String name;

    @OneToMany(mappedBy = "status")
    private List<Project> projects;

    public ProjectStatus(String name) {
        this.name = name;
    }
}
