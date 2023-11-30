package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.dto.SubDomainDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubDomain {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(unique=true)
    private String subdomain;
    @Column(columnDefinition = "DEFAULT FALSE")
    private Boolean isValidated;
    @Column(columnDefinition = "DEFAULT FALSE")
    private Boolean isTaken;
    private Boolean isCustomerDomain;

    public SubDomainDto toSubDomainDTO() {
        return new SubDomainDto(this.subdomain, this.isValidated, this.isTaken, this.isCustomerDomain);
    }
}
