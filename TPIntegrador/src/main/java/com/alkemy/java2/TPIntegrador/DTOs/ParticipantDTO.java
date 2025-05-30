package com.alkemy.java2.TPIntegrador.DTOs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
    private String userId;
    private String status; // confirmed / pending / declined
}

