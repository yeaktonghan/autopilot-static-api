package com.kshrd.autopilot.entities;

import jakarta.persistence.*;

@Entity
public class NotificationCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String botToken;
    private String botId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id",referencedColumnName = "id")
    private Project project;
}
