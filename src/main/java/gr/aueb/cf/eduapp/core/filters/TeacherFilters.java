package gr.aueb.cf.eduapp.core.filters;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TeacherFilters {
    private UUID uuid;
    private String vat;
    private String amka;
    private String firstname;
    private String lastname;
    private String region;
    private boolean deleted;
}
