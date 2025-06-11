package com.alkemy.java2.TPIntegrador.model;

import com.alkemy.java2.TPIntegrador.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {
    private String userId;
    private Status status;
}