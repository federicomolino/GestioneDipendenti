package com.gestioneDipendeti.GestioneDipendenti.DTOAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UtenteDTO {

    @NotBlank(message = "Username richiesto")
    private String username;

    @NotBlank(message = "Devi inserire una password")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Non può essere vuota")
    private String email;

    @JsonProperty("role")
    private List<RoleDTO> roleDTO;

    public List<RoleDTO> getRoleDTO() {
        return roleDTO;
    }

    public void setRoleDTO(List<RoleDTO> roleDTO) {
        this.roleDTO = roleDTO;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
