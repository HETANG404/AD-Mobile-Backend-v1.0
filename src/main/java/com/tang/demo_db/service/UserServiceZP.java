package com.tang.demo_db.service;

import com.tang.demo_db.dao.UserDAO;
import com.tang.demo_db.dto.UserProfileDTO;
import com.tang.demo_db.entity.*;
import com.tang.demo_db.repository.DailyViewRepository;
import com.tang.demo_db.repository.UserRepository;
import com.tang.demo_db.repository.UserSessionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class UserServiceZP {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private final DailyViewRepository dailyViewRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceZP(DailyViewRepository dailyViewRepository) {
        this.dailyViewRepository = dailyViewRepository;
    }

    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    public User getUserById(Long id) {
        return userDAO.findUserById(id);
    }
    /**
     * 用户登录
     */
    public User login(String username, String password, String ipAddress) {
        // 查询用户
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 如果存在老的会话，删除它
            List<UserSession> existingSessions = userSessionRepository.findByUser(user);
            for (UserSession session : existingSessions) {
                if (!session.getIpAddress().equals(ipAddress)) {
                    // 删除之前的会话记录
                    userSessionRepository.delete(session);
                }
            }

            // 创建新的会话记录
            UserSession newSession = new UserSession();
            newSession.setUser(user);
            newSession.setIpAddress(ipAddress);
            newSession.setLoginTime(LocalDateTime.now());

            // 保存新的会话信息
            userSessionRepository.save(newSession);

            return user; // 登录成功
        }
        return null; // 登录失败
    }


    /**
     * 用户注册
     */
    public User register(String username, String rawPassword, String email, int age) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists"); // 用户名已存在
        }

        // 检查邮箱是否已存在
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists"); // 邮箱已存在
        }

        // 密码验证：需要大写字母、小写字母和数字
        if (!isValidPassword(rawPassword)) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter, one uppercase letter, one digit, and be at least 6 characters long");
        }

        // 使用 BCrypt 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setEmail(email);
        newUser.setAge(age);

        try {
            return userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            // 处理数据库违反唯一约束的错误
            throw new IllegalArgumentException("Data integrity violation occurred");
        }
    }

    // 密码验证方法
    private boolean isValidPassword(String password) {
        // 正则表达式：至少一个大写字母、一个小写字母和一个数字
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}$";
        return Pattern.matches(passwordRegex, password);
    }



    /**
     * 检查原密码是否正确
     */
    public boolean checkOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    /**
     * 修改密码
     */
    public boolean changePassword(User user, String newPassword) {
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true; // 密码修改成功
    }







    /**
     * 重置用户密码，并发送到邮箱
     */
    public boolean resetPassword(User user) {
        if (user == null) {
            return false;
        }

        // 1. 生成新密码
        String newPassword = generateRandomPassword();

        // 2. 加密新密码并更新数据库
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 3. 发送邮件通知用户
        return sendResetPasswordEmail(user.getEmail(), newPassword);
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) { // 生成10位随机密码
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    /**
     * 发送重置密码邮件
     */
    private boolean sendResetPasswordEmail(String toEmail, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Your Password Has Been Reset");
            helper.setText("<p>Your new password is: <b>" + newPassword + "</b></p>", true);

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取用户偏好
     */
    public List<Preference> getUserPreferences(Long userId) {
        User user = getUserById(userId);
        return user.getPreferences();
    }

    /**
     * 更新用户偏好，确保字段对应，并转换前端的 pricePreference
     */
    @Transactional
    public Preference updateUserPreferences(Long userId, String diet, List<String> cuisines, String price) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查是否已有偏好
        List<Preference> preferences = user.getPreferences();
        Preference preference;

        if (preferences == null || preferences.isEmpty()) {
            preference = new Preference();
            preference.setUser(user);
            user.getPreferences().add(preference);
        } else {
            preference = preferences.get(0); // 只修改第一个 Preference
        }

        // 设置偏好信息
        preference.setDietPreference(diet != null ? diet : "No dietary restrictions");
        preference.setCuisinesPreference((cuisines != null && !cuisines.isEmpty()) ? cuisines.get(0) : "Unknown");
        preference.setPricePreference(convertPricePreference(price));

        return preference; // Hibernate 自动持久化
    }



    /**
     * 处理前端 pricePreference 转换为后端 PricePreference 枚举
     */
    private Preference.PricePreference convertPricePreference(String price) {
        if (price == null) {
            return Preference.PricePreference.MEDIUM; // 默认值
        }
        return switch (price.toLowerCase()) {
            case "$ affordable" -> Preference.PricePreference.LOW;
            case "$ $ moderate" -> Preference.PricePreference.MEDIUM;
            case "$ $ $ expensive" -> Preference.PricePreference.HIGH;
            default -> Preference.PricePreference.MEDIUM; // 默认值
        };
    }
    public boolean updateUserIsNew(String userId) {
        Optional<User> optionalUser = userRepository.findById(Long.parseLong(userId));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsNew(false); // 设置 isNewUser 为 false
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public void recordSearchCountOfDailyView(Integer searchCount) {
        LocalDate today = LocalDate.now();

        // 查找今天的记录
        DailyView dailyView = dailyViewRepository.findByDate(today)
                .orElse(new DailyView(null, today, 0, 0));

        // 更新 searchCount
        dailyView.setSearchCount(dailyView.getSearchCount() + searchCount);

        // 保存到数据库
        dailyViewRepository.save(dailyView);
    }
    public void recordLoginCountToDailyView(Integer logoutCount) {
        LocalDate today = LocalDate.now();

        // 查找今天的记录
        DailyView dailyView = dailyViewRepository.findByDate(today)
                .orElse(new DailyView(null, today, 0, 0));

        // 更新 loginCount
        dailyView.setLoginCount(dailyView.getLoginCount() + logoutCount);

        // 保存到数据库
        dailyViewRepository.save(dailyView);
    }

    // Get user profile by ID (or username, if needed)
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserProfileDTO(user.getUsername(), user.getEmail(), user.getAge());
    }

    // Update user profile
    public void updateUserProfile(Long userId, UserProfileDTO userProfileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userProfileDTO.getUsername());
        user.setEmail(userProfileDTO.getEmail());
        user.setAge(userProfileDTO.getAge());
        userRepository.save(user); // Save the updated user profile
    }
}
