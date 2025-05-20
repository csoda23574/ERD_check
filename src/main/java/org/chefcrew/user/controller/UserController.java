package org.chefcrew.user.controller;

import lombok.RequiredArgsConstructor;
import org.chefcrew.user.dto.request.SignUpRequest;
import org.chefcrew.user.dto.response.GetUserInfoResponse;
import org.chefcrew.user.dto.response.LoginResponse;
import org.chefcrew.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserInfoResponse> getUserInfo(@PathVariable("userId") long userId) {
        return ResponseEntity.ok().body(userService.getUserInfo(userId));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest requestBody) {
        userService.addUser(requestBody);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody SignUpRequest requestBody){
        return ResponseEntity.ok(userService.login(requestBody));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") long userId, @RequestBody Map<String, Object> updates) {
        userService.updateUser(userId, updates);
        return ResponseEntity.ok().build();
    }
}