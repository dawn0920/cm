package org.example.cm.domain.user.entity;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cm.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserProvider provider;

    @Setter
    private String providerId;

    @Column(columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
    private boolean isDeleted;

    @Builder
    public User(String name, String email, String password, LocalDate birthDate, UserGender gender, String address, String phone, UserProvider provider, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.provider = provider;
        this.role = role;
    }
=======
public class User {
>>>>>>> 847b7e84545189f4e515306ea902692a677b0206
}
