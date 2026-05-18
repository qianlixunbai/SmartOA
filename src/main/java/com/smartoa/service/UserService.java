package com.smartoa.service;

import com.smartoa.entity.User;
import com.smartoa.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    public User getLoginUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }
}
