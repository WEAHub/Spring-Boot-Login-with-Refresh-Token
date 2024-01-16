package com.jwt.login.repository;

import com.jwt.login.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {

    Optional<UserInfo> findByName(String userName);
    Optional<UserInfo> findByEmail(String email);
    Boolean existsByName(String name);


}
