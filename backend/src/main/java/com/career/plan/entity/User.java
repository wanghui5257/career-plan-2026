package com.career.plan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Getter @Setter
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 255)
    @Getter @Setter
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    @Getter @Setter
    private String password;
    
    @Column(name = "email", length = 255)
    @Getter @Setter
    private String email;
    
    @Column(name = "role_id")
    @Getter @Setter
    private Long roleId;
    
    @Column(name = "background", columnDefinition = "TEXT")
    @Getter @Setter
    private String background;
    
    @Column(name = "goals", columnDefinition = "TEXT")
    @Getter @Setter
    private String goals;
    
    @Column(name = "created_at")
    @Getter @Setter
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Getter @Setter
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
