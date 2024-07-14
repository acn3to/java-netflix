package com.netflix.repositories;

import com.netflix.entities.User;

public interface UserRepository extends Repository<User> {
    User findByEmail(String email);
}
