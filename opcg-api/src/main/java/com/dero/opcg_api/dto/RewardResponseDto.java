package com.dero.opcg_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardResponseDto {
    private String message;
    private int newBalance;
}