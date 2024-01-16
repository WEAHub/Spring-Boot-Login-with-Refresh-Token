package com.jwt.login.service;

import com.jwt.login.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoDetails implements UserDetails {
  String userName = null;
  String password = null;
  Integer id = null;
  List<GrantedAuthority> authorities;

  public UserInfoDetails(UserInfo userInfo){
    id = userInfo.getId();
    userName = userInfo.getName();
    password = userInfo.getPassword();
    authorities = Arrays.stream(userInfo.getRoles().split(","))
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
  }
  
  public Integer getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
