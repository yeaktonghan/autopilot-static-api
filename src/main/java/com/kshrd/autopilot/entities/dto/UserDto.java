package com.kshrd.autopilot.entities.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kshrd.autopilot.entities.user.Gender;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imageUrl;
    private Gender gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;
    public UserDto(Long id,String username,String fullName,String email,String imageUrl,Gender gender){
        this.id=id;
        this.username=username;
        this.fullName=fullName;
        this.email=email;
        this.imageUrl=imageUrl;
        this.gender=gender;
    }

}
