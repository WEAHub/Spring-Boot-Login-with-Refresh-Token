package com.jwt.login.controller;

import com.jwt.login.entity.AuthRequest;
import com.jwt.login.entity.UserInfo;
import com.jwt.login.service.JwtService;
import com.jwt.login.service.UserInfoDetails;
import com.jwt.login.service.UserInfoService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

  @Autowired
  private UserInfoService userInfoService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  @PostMapping("/addUser")
  public Map<String, String> addUser(@RequestBody UserInfo userInfo) throws BadRequestException{

    Boolean alreadyExist = userInfoService.userExists(userInfo.getName());

    if(alreadyExist){
      throw new BadRequestException("User already exists");
    }

    userInfoService.addUser(userInfo);

    return Collections.singletonMap("message", "success");
  }

  @PostMapping(
    value = "/login", 
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Map<String, String> login(@RequestBody AuthRequest authRequest){

    UsernamePasswordAuthenticationToken userAuthToken = 
      new UsernamePasswordAuthenticationToken(
        authRequest.getUserName(), 
        authRequest.getPassword()
      );

    Authentication authenticate = authenticationManager.authenticate(userAuthToken);
    
    if(!authenticate.isAuthenticated()){
      throw new UsernameNotFoundException("Invalid user request");
    }

    String userName = authRequest.getUserName();
    String jwtToken = jwtService.generateAccessToken(userName);
    String jwtRefreshToken = jwtService.generateRefreshToken(userName);

    HashMap<String, String> jsonResponse = new HashMap<>();
    jsonResponse.put("accessToken", jwtToken);
    jsonResponse.put("refreshToken", jwtRefreshToken);

    return jsonResponse;
  }

  @GetMapping("/refresh")
  public Map<String, String> refreshToken() {
    
    UserInfoDetails userDetails = (UserInfoDetails)SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();
    
    String userName = userDetails.getUsername();
    String jwtToken = jwtService.generateAccessToken(userName);
    String jwtRefreshToken = jwtService.generateRefreshToken(userName);

    HashMap<String, String> jsonResponse = new HashMap<>();
    jsonResponse.put("accessToken", jwtToken);
    jsonResponse.put("refreshToken", jwtRefreshToken);

    return jsonResponse;
  }

  @GetMapping("/getUsers")
  @PreAuthorize("hasAuthority('ADMIN_ROLES')")
  public List<UserInfo> getAllUsers(){
    return userInfoService.getAllUser();
  }

  @GetMapping("/getUsers/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN_ROLES','USER_ROLES')")
  public UserInfo getAllUsers(@PathVariable Integer id) throws BadRequestException {

    UserInfoDetails userDetails = (UserInfoDetails)SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();
    
    Boolean canGetUser = 
      userDetails.getAuthorities()
        .stream()
        .anyMatch(auth -> auth.getAuthority().equals("ADMIN_ROLES")) ||  
      userDetails.getId() == id;

    
    if(!canGetUser) {
      throw new BadRequestException("User already exists");
    }

    UserInfo userFound = userInfoService.getUser(id);

    return userFound;
  }

  @GetMapping("/deleteUser/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN_ROLES','USER_ROLES')")
  public Map<String, String> deleteUser(@PathVariable Integer id) throws BadRequestException {
    
    UserInfoDetails userDetails = (UserInfoDetails)SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();

    Boolean canGetUser = 
      userDetails.getAuthorities()
        .stream()
        .anyMatch(auth -> auth.getAuthority().equals("ADMIN_ROLES")) ||  
      userDetails.getId() == id;
    
    String deleteMessage = "failed";

    if(canGetUser) {
      userInfoService.deleteUserById(id);
      deleteMessage = "success";
    }

    return Collections.singletonMap("message", deleteMessage);
  }

}
