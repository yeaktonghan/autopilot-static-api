package com.kshrd.autopilot.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.eclipse.jgit.internal.storage.file.PackReverseIndex;

@Entity
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    @OneToOne
    private DeploymentApp deploymentApp;
}
