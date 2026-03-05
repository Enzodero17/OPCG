package com.dero.opcg_api.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}