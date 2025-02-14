package com.tang.demo_db.controller;

import com.tang.demo_db.dto.ChangePasswordRequestDTO;
import com.tang.demo_db.dto.LoginRequest;
import com.tang.demo_db.dto.RegisterRequestDTO;
import com.tang.demo_db.dto.UserProfileDTO;
import com.tang.demo_db.entity.Preference;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.entity.UserSession;
import com.tang.demo_db.repository.UserSessionRepository;
import com.tang.demo_db.service.UserServiceZP;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
public class UserControllerZP {

    @Autowired
    private UserServiceZP userService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String ipAddress = getClientIpAddress(); // 获取当前设备的IP地址
        User loggedInUser = (User) session.getAttribute("user");
        System.out.println(loggedInUser);
        if (session.getAttribute("user") != null) {
            return ResponseEntity.status(HttpStatus.OK).body("Already logged in");
        }

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword(), ipAddress);

        if (user != null) {
            session.setAttribute("user", user); // 记录用户 Session

            // 构建 JSON 响应对象
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());  // 假设 User 类有 getId() 方法
            userData.put("username", user.getUsername());
            userData.put("isNew", false);  // 登录的用户通常不是新用户，除非你有逻辑判断

            response.put("user", userData);

            userService.recordLoginCountToDailyView(1);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }

    // 获取当前请求的 IP 地址
    private String getClientIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }


    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            User newUser = userService.register(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getAge()
            );

            if (newUser != null) {
                // 构建 JSON 响应对象
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);

                Map<String, Object> userData = new HashMap<>();
                userData.put("id", newUser.getId());  // 假设 User 类有 getId() 方法
                userData.put("username", newUser.getUsername());
                userData.put("isNew", true);

                response.put("user", userData);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Username already exists"));
            }

        } catch (IllegalArgumentException e) {
            // 捕获密码验证错误并返回详细的错误信息
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // 捕获其他未知的异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred"));
        }
    }




    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 获取当前用户
        User user = (User) session.getAttribute("user");

        if (user != null) {
            List<UserSession> userSessions = userSessionRepository.findByUser(user);
            if (!userSessions.isEmpty()) {
                userSessionRepository.deleteAll(userSessions);
                System.out.println("删除用户会话: " + userSessions.size() + " 个");
            }

            System.out.println("用户 " + user.getId() + " 退出登录");

//            userService.recordLoginCountToDailyView(1);

            // 清除 Session
            session.invalidate();


            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("message", "Logged out successfully."));
        }

        // 如果用户没有登录（session 为空）
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("error", "No user is currently logged in"));
    }





    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, HttpSession session) {
        // 检查用户是否已登录
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User is not logged in"));
        }

        System.out.println(changePasswordRequestDTO.getOldPassword());
        System.out.println(changePasswordRequestDTO.getNewPassword());

        // 原密码验证
        boolean isOldPasswordValid = userService.checkOldPassword(loggedInUser, changePasswordRequestDTO.getOldPassword());
        if (!isOldPasswordValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Incorrect old password"));
        }

        // 新密码与确认密码检查
        if (!changePasswordRequestDTO.getNewPassword().equals(changePasswordRequestDTO.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "New passwords do not match"));
        }

        // 新密码验证
        boolean isNewPasswordValid = isValidPassword(changePasswordRequestDTO.getNewPassword());
        if (!isNewPasswordValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "New password does not meet the required criteria"));
        }

        // 确保新密码与原密码不同
        if (changePasswordRequestDTO.getOldPassword().equals(changePasswordRequestDTO.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "New password cannot be the same as the old password"));
        }

        // 修改密码
        boolean isPasswordChanged = userService.changePassword(loggedInUser, changePasswordRequestDTO.getNewPassword());
        if (isPasswordChanged) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("message", "Password changed successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to change password"));
        }
    }



    private boolean isValidPassword(String password) {
        // 正则表达式：至少一个大写字母、一个小写字母和一个数字
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}$";
        return Pattern.matches(passwordRegex, password);
    }


    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(HttpSession session) {
        // 1. 从 Session 获取当前用户
        User loggedInUser = (User) session.getAttribute("user");

        // 2. 确保用户已登录
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }

        // 3. 调用服务层方法重置密码
        boolean isReset = userService.resetPassword(loggedInUser);
        if (isReset) {
            return ResponseEntity.ok("Password reset successfully. Check your email for the new password.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password");
        }
    }


    @PostMapping("getPreferences/{userId}")
    public ResponseEntity<List<Preference>> getUserPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserPreferences(userId));
    }

    /**
     * 更新用户偏好
     */
    @PostMapping("updatePreferences/{userId}")
    public ResponseEntity<?> savePreferences(
            @PathVariable Long userId,
            @RequestParam(required = false) String dietPreference,
            @RequestParam(required = false) List<String> preferredCuisines,
            @RequestParam(required = false) String pricePreference) {

        Preference updatedPreference = userService.updateUserPreferences(userId, dietPreference, preferredCuisines, pricePreference);
        return ResponseEntity.ok(updatedPreference);
    }


    @PatchMapping("/update-isnew/{userId}")
    public ResponseEntity<?> updateUserIsNew(@PathVariable String userId) {
        boolean updated = userService.updateUserIsNew(userId);
        if (updated) {
            return ResponseEntity.ok(Collections.singletonMap("message", "isNewUser 更新成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "用户未找到"));
        }
    }

    //每次点击搜索，记录一次当天的搜索次数到dailyview
    @PostMapping("/updateSearchCountOfDailyView")
    public ResponseEntity<?> updateSearchCountOfDailyView(@RequestBody Map<String, Integer> requestData) {
        if (!requestData.containsKey("searchCount")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Missing searchCount"));
        }
        int searchCount = requestData.get("searchCount");
        userService.recordSearchCountOfDailyView(searchCount);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("message", "Search count recorded successfully."));
    }

    // Endpoint to get user profile
    // Endpoint to get user profile
    @GetMapping("/fetchProfile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        try {
            UserProfileDTO userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Respond with 404 if user is not found
        }
    }

    // Endpoint to update user profile
    @PutMapping("/updateProfile/{userId}")
    public ResponseEntity<Void> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfileDTO userProfileDTO) {
        try {
            userService.updateUserProfile(userId, userProfileDTO);
            return ResponseEntity.noContent().build();  // Respond with 204 No Content after a successful update
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Respond with 404 if user is not found
        }
    }


}