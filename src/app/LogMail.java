package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LogMail extends Application {
    private final int NUMTHREAD = 4;
    private ExecutorService ex;
    @Override
    public void start(Stage primaryStage){
        Log l = new Log();
        l.start();
    }

    class Log extends Thread{
        /*per aggiungere nomi nel campo user bisogna creare un file txt con abc@def.ff nella
        * directory /resources/mails e nella directory mails successivamente aggiungere la stringa
        * nella variabile user */
        private final String[] user = {"marco@unito.it","rossi@unito.it"};
        @Override
        public void run(){
            int i =0;
            ex = Executors.newFixedThreadPool(NUMTHREAD);
            while(i<3){
                ex.execute(new LogIn(user[i]));
                i++;
            }
        }
        class LogIn  implements Runnable {
            private final String email;
            private final Client client;

            public LogIn(String s) {
                email =s;
                client = new Client(email);
            }
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new ClientApp(email,client,ex);
                    }
                });
            }
        }
    }
}
