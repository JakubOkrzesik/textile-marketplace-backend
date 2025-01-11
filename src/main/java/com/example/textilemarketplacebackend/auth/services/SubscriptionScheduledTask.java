package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.global.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduledTask {

    private final UserService userService;

    @Scheduled(cron = "@midnight")
    public void scheduledTask() {
        List<User> users = userService.findAll();
        users.forEach(user -> {
            if (user.getSubscription() != null && !user.getSubscription().isActive()) {
                user.setActivated(false);
                userService.save(user);
            }
        });
    }
}
