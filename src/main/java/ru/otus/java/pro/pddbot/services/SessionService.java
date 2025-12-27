package ru.otus.java.pro.pddbot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.java.pro.pddbot.model.ExamSession;
import ru.otus.java.pro.pddbot.repository.ExamSessionRepository;

@Service
@Slf4j
public class SessionService {

    private final ExamSessionRepository examSessionRepository;

    private final UserService userService;

    @Autowired
    public SessionService(ExamSessionRepository examSessionRepository, UserService userService) {
        this.examSessionRepository = examSessionRepository;
        this.userService = userService;
    }

    public Iterable<ExamSession> findAll(){
        Iterable<ExamSession> sessions = examSessionRepository.findAll();
        sessions.forEach(this::joinUser);
        return sessions;
    }

    private void joinUser(ExamSession examSession){
        if(examSession.getUserId()!=null){
            userService.findById(examSession.getUserId()).ifPresent(examSession::setUser);
        }
    }

    public void deleteSessionById(Long id){
        examSessionRepository.deleteById(id);
    }
}
