package com.gl.userservice.repository;

import com.gl.userservice.entity.Role;
import com.gl.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByRoleAndVerified(Role role, Boolean verified);

    List<User> findByRole(Role role);
    long countByRole(Role role);
    long countByRoleAndVerified(Role role, Boolean verified);
}