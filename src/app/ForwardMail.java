package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;

import java.io.IOException;
import java.util.Objects;

public class ForwardMail {
    private static Client client;
    private static String txt;
    private static String who_for;
    public ForwardMail(Client c,String text,String who) throws IOException {
        txt=text;
        client = c;
        who_for=who;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/forward.fxml")));
        Scene scene = new Scene(root, 613, 400);
        Stage stage = new Stage();
        stage.setTitle("INOLTRO");
        stage.setScene(scene);
        stage.show();
    }
    public static Client getClient(){return client;}

    public static String getTxt() {return txt;}

    public static String getWho_for() {return who_for;}
}
