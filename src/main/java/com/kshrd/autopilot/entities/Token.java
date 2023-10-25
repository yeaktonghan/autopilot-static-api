package com.kshrd.autopilot.entities;

import jakarta.persistence.*;
import org.eclipse.jgit.internal.storage.file.PackReverseIndex;

@Entity

public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    @OneToOne
    private DeploymentApp deploymentApp;
}
