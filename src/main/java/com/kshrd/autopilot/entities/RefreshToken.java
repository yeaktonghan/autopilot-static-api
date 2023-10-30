package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshToken {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String token;
    private Instant expireDate;
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

}
