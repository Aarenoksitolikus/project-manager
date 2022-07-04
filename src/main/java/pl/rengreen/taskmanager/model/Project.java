package pl.rengreen.taskmanager.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @NotEmpty(message = "{project.name.not.empty}")
    private String name;

    @NotEmpty(message = "{project.description.not.empty}")
    @Column(length = 1200)
    @Size(max = 1200, message = "{project.description.size}")
    private String description;

    @NotNull(message = "{project.date.not.null}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "status")
    private ProjectStatus status;

    private String creatorName;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> participants;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    public Project(String name, ProjectStatus status) {
        this.name = name;
        this.status = status;
    }

    public boolean isCompleted() {
        return Objects.equals(status.getName(), "Done");
    }

    public long daysLeftUntilDeadline(LocalDate date) {
        return ChronoUnit.DAYS.between(LocalDate.now(), date);
    }
}
