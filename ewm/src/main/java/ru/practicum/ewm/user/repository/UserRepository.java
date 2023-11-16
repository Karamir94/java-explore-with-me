package ru.practicum.ewm.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByName(String name);
}
