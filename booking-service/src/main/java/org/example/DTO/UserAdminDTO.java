package org.example.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAdminDTO {
    private Long id;
    private String username;
    private String role;
    private LocalDateTime createdAt;
}
