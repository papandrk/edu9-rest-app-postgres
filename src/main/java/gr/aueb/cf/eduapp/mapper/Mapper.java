package gr.aueb.cf.eduapp.mapper;

import gr.aueb.cf.eduapp.dto.*;
import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public User mapToUserEntity(UserInsertDTO insertDTO) {

        // TODO : doesn't insertDTO.roleId also need to be mapped in this method??? -> (guess at answer) no, because the role already exists and it is not created during user creation - the roleId must be used to retrieve the role via the RoleRepository (and for some reason that i don't know (perhaps it's just preference), that is to be done in the UserService, NOT here in the mapper)

        return new User(insertDTO.username(), insertDTO.password());
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(user.getUuid().toString(), user.getUsername(), user.getRole().getName());
    }

    public Teacher mapToTeacherEntity(TeacherInsertDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setFirstname(dto.firstname());
        teacher.setLastname(dto.lastname());
        teacher.setVat(dto.vat());

        UserInsertDTO userDTO = dto.userInsertDTO();
        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword(userDTO.password());
        teacher.tieToUser(user);                // Set User entity on Teacher (two-way relation - using convenience method)

        PersonalInfoInsertDTO personalInfoDTO = dto.personalInfoInsertDTO();
        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setAmka(personalInfoDTO.amka());
        personalInfo.setIdentityNumber(personalInfoDTO.identityNumber());
        personalInfo.setPlaceOfBirth(personalInfoDTO.placeOfBirth());
        personalInfo.setMunicipalityOfRegistration(personalInfoDTO
                .municipalityOfRegistration());
        teacher.setPersonalInfo(personalInfo);  // Set PersonalInfo entity on Teacher (one-way relation)

        // TODO : doesn't dto.regionId also need to be mapped in this method??? -> (guess at answer) no, because the region already exists and it is not created during teacher creation - the regionId must be used to retrieve the regio via the RegionRepository (and for some reason that i don't know (perhaps it's just preference), that is to be done in the TeacherService, NOT here in the mapper)

        return teacher;
    }

    public TeacherReadOnlyDTO mapToTeacherReadOnlyDTO(Teacher teacher) {
        return new TeacherReadOnlyDTO(teacher.getUuid().toString(), teacher.getFirstname(), teacher.getLastname(),
                teacher.getVat(), teacher.getRegion().getName());
    }

}
