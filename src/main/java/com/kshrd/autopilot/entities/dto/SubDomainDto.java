package com.kshrd.autopilot.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubDomainDto {
    private String subdomain;
    private Boolean isValidated;
    private Boolean isTaken;
    private Boolean isCustomerDomain;
}
