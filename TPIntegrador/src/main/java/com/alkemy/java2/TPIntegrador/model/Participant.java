package com.alkemy.java2.TPIntegrador.model;

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
    private String status; // "confirmed", "pending", "declined"
}