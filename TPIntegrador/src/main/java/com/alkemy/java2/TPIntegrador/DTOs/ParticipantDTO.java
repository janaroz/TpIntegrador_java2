package com.alkemy.java2.TPIntegrador.DTOs;
import com.alkemy.java2.TPIntegrador.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
    private String userId;
    private Status status; // confirmed / pending / declined
}

