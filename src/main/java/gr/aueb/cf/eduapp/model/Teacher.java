package gr.aueb.cf.eduapp.model;

import gr.aueb.cf.eduapp.model.static_data.Region;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "teachers")
public class Teacher extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(unique = true)
    private String vat;

    private String firstname;
    private String lastname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_info_id") // 'unique = true' not needed since the relationship is flagged as
                                           // one-to-one - hibernate takes care that the id is unique
    private PersonalInfo personalInfo;

    @PrePersist
    public void initializeUUID() {
        this.uuid = UUID.randomUUID();
    }

    public void tieToUser(User user) {
        this.user = user;
        user.setTeacher(this);
    }

    public void untieFromUser() { // can this exist/be run even though the user field has 'nullable = false'?
        this.user.setTeacher(null);
        this.user = null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Teacher teacher)) return false;
        return Objects.equals(getUuid(), teacher.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid());
    }
}
