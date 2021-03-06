package org.cubegame.infrastructure;

import org.cubegame.infrastructure.bots.CubeGameBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotRunner {

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new CubeGameBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
