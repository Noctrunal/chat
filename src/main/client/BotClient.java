package main.client;

import main.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message.contains(":")) {
                Calendar calendar = Calendar.getInstance();
                String[] parts = message.split(":");
                String userName = parts[0];
                String textMessage = parts[1];

                switch (textMessage.trim().toLowerCase()) {
                    case "дата":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("d.MM.yyyy").format(calendar.getTime())));
                        break;
                    case "день":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("d").format(calendar.getTime())));
                        break;
                    case "месяц":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("MMMM").format(calendar.getTime())));
                        break;
                    case "год":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("yyyy").format(calendar.getTime())));
                        break;
                    case "время":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("H:mm:ss").format(calendar.getTime())));
                        break;
                    case "час":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("H").format(calendar.getTime())));
                        break;
                    case "минуты":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("m").format(calendar.getTime())));
                        break;
                    case "секунды":
                        sendTextMessage(String.format("Информация для %s: %s", userName, new SimpleDateFormat("s").format(calendar.getTime())));
                        break;
                }
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        int randomUserName = (int) (Math.random() * 99);
        return String.format("dat_bot_%d", randomUserName);
    }

    public static void main(String[] args) {
        new BotClient().run();
    }
}
