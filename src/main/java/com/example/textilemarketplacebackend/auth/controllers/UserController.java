package com.example.textilemarketplacebackend.auth.controllers;

import com.example.textilemarketplacebackend.auth.models.user.UserDto;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.global.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final ResponseHandlerService responseService;
    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<Object> getUserData(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            return responseService.generateResponse("User data fetched", HttpStatus.OK, userService.getUserData(authHeader));
        } catch (Exception e) {
            return responseService.generateResponse("Error while fetching user data", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/edit")
    public ResponseEntity<Object> editUserData(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody UserDto userDto) {
        try {
            userService.editUserData(authHeader, userDto);
            return responseService.generateResponse("User data fetched", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseService.generateResponse("User data fetched", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
