package ru.otus.java.pro.pddBot.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.pro.pddBot.model.User;
import ru.otus.java.pro.pddBot.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Slf4j
public class UserService {

   private final UserRepository userRepository;

   @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User getOrCreateUser(Long telegramId, Long chatId, String username, String firstName) {

        Optional<User> existingUser = userRepository.findByTelegramId(telegramId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setChatId(chatId);
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }

        User newUser = new User();
        newUser.setTelegramId(telegramId);
        newUser.setChatId(chatId);
        newUser.setUsername(username);
        newUser.setFirstName(firstName);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        log.info("Создан новый пользователь: {} ({})", firstName, telegramId);
        return userRepository.save(newUser);
    }

    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    @Transactional
    public void updateUserState(Long telegramId, String state) {
        userRepository.updateUserState(telegramId, state);
    }

    @Transactional
    public void updateStatistics(Long userId, boolean isCorrect) {
        int increment = isCorrect ? 1 : 0;
        userRepository.updateStatistics(userId, increment);
    }

    @Transactional
    public void incrementExamCount(Long userId, boolean passed) {
        userRepository.incrementTotalExams(userId);
        if (passed) {
            userRepository.incrementPassedExams(userId);
        }
    }

    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public long getTotalUsers() {
        return userRepository.countActiveUsers();
    }
}
