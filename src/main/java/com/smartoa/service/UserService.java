package com.smartoa.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartoa.config.UserContextHolder;
import com.smartoa.entity.User;
import com.smartoa.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getLoginUser() {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }
}
