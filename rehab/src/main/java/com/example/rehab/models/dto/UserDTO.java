package com.example.rehab.models.dto;

import com.example.rehab.models.enums.UserRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class UserDTO {

    private Long id;

    @NotEmpty(message = "Please, enter username")
    @Size(min = 3,message = "Username must contains at least three characters")
    private String username;

    @NotEmpty(message = "Please, enter password")
    @Size(min = 6,message = "Password must contains at least six characters")
    private String password;

    @NotEmpty(message = "Password mismatch")
    private String confirmPassword;

    private UserRole userRole;

    public UserDTO(Long id, String username, String password, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }


    public void setPassword(String password) {
        this.password = password;
        checkPasswordAreEqual();
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        checkPasswordAreEqual();
    }

    private void checkPasswordAreEqual() {
        boolean isNull = password == null || confirmPassword == null;
        if (!isNull && !password.equals(confirmPassword)) {
            confirmPassword = "";
        }
    }
}
