package com.tang.demo_db.controller;

import com.tang.demo_db.entity.UserPreference;
import com.tang.demo_db.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    @Autowired
    private UserPreferenceService preferenceService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> savePreferences(@PathVariable String userId, @RequestBody UserPreference preference) {
        return ResponseEntity.ok(preferenceService.savePreferences(userId, preference));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPreferences(@PathVariable String userId) {
        return ResponseEntity.ok(preferenceService.getPreferences(userId));
    }
}
