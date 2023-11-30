package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.SubDomainDto;

import java.util.List;

public interface SubDomainService {
    List<SubDomainDto> getAllSubDomain();

    List<SubDomainDto> getAvailableSubDomain();
}
