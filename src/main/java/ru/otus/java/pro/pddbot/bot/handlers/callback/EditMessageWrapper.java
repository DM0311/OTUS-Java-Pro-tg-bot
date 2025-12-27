package ru.otus.java.pro.pddbot.bot.handlers.callback;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@NoArgsConstructor
@Getter
@Setter
public class EditMessageWrapper {

    private EditMessageText editMessageText;

    private boolean isFinalMessage;

}
