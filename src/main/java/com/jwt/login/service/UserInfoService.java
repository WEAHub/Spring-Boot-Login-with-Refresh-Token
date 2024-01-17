package com.jwt.login.service;

import com.jwt.login.entity.UserInfo;
import com.jwt.login.repository.UserInfoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<UserInfo> userInfo = userInfoRepository.findByName(username);
      return userInfo.map(UserInfoDetails::new)
        .orElseThrow(() -> 
          new UsernameNotFoundException("User not found" + username)
        );
    }

    public UserInfo addUser(UserInfo userInfo) {
      String password = userInfo.getPassword();
      String encodedPassword = passwordEncoder.encode(password);
      userInfo.setPassword(encodedPassword);
      return userInfoRepository.save(userInfo);
    }

    public List<UserInfo> getAllUser(){
      return userInfoRepository.findAll();
    }

    public UserInfo getUser(Integer id){
      return userInfoRepository
        .findById(id)
        .get();
    }

    public Boolean userExists(String name) {
      return userInfoRepository
        .existsByName(name);
    }

    public void deleteUserById(Integer id) {
      userInfoRepository.deleteById(id);
    }
}
