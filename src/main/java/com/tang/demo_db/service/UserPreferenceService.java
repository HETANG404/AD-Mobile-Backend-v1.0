package com.tang.demo_db.service;

import com.tang.demo_db.entity.UserPreference;
import com.tang.demo_db.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository; // ✅ 添加 Repository 依赖

    /**
     * 保存用户偏好
     * @param userId 用户 ID
     * @param preference 偏好设置
     * @return 保存后的用户偏好
     */
    public UserPreference savePreferences(String userId, UserPreference preference) {
        return userPreferenceRepository.save(preference);  // ✅ 返回保存后的对象
    }

    /**
     * 获取用户偏好
     * @param userId 用户 ID
     * @return 用户偏好对象（如果存在）
     */
    public UserPreference getPreferences(String userId) {
        return userPreferenceRepository.findById(userId).orElse(null);  // ✅ 确保返回正确的类型
    }
}
