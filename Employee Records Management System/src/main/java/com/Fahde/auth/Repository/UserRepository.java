package com.Fahde.auth.Repository;

import java.util.Optional;

import com.Fahde.auth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByUsername(String email);

}
