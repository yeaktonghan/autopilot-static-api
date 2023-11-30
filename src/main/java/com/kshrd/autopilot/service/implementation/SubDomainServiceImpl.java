package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.DeploymentApp;
import com.kshrd.autopilot.entities.SubDomain;
import com.kshrd.autopilot.entities.dto.SubDomainDto;
import com.kshrd.autopilot.repository.SubDomainRepository;
import com.kshrd.autopilot.service.SubDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubDomainServiceImpl implements SubDomainService {
    private final SubDomainRepository subDomainRepository;

    public SubDomainServiceImpl(SubDomainRepository subDomainRepository) {
        this.subDomainRepository = subDomainRepository;
    }

    @Override
    public List<SubDomainDto> getAllSubDomain() {
        return subDomainRepository.findAll().stream().map(SubDomain::toSubDomainDTO).toList();
    }

    @Override
    public List<SubDomainDto> getAvailableSubDomain() {
        return subDomainRepository.findAllByIsTakenFalse().stream().map(SubDomain::toSubDomainDTO).toList();
    }
}
