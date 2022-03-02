package app;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Client;


import java.util.Objects;
import java.util.concurrent.ExecutorService;


public class ClientApp  {
    private static String mailUser;
    private static Client client;
    private ExecutorService exec;

    public ClientApp(String email, Client c, ExecutorService ex){
        mailUser = email;
        client = c;
        exec = ex;
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/client.fxml")));
            Scene scene = new Scene(root, 650, 450);
            Stage stage = new Stage();
            stage.setTitle("CLIENT " + mailUser);
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    Thread.currentThread().interrupt();
                    exec.shutdown();
                }
            });
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public static String getUser(){return mailUser;}
    public static Client getClient(){return client;}
}
