package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;
@Entity
@Data
public class ConfirmationEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Integer tokenid;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationEmail(User user) {
        this.user = user;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

    public ConfirmationEmail() {

    }
}
