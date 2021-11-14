package com.example.rehab.models.dto;

import com.example.rehab.models.User;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class UserDTO {

    private Long id;

    @NotEmpty(message = "Please, enter username")
    private String username;

    @NotEmpty(message = "Please, enter password")
    @Size(min = 6,message = "Password must be at least six characters")
    private String password;

    private UserRole userRole;

    public UserDTO(Long id, String username, String password, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }

    public String getUsername(){
        return username;
    }

}
