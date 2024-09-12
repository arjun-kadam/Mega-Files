package com.megafiles.repository;

import com.megafiles.entity.Users;
import com.megafiles.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByEmail(String email);
    Users findByRole(Roles email);
}
