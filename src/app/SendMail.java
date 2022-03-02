package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;

import java.io.IOException;

public class SendMail {

    private static Client client;

    public SendMail(Client c) throws IOException {
        client = c;
        Parent root = FXMLLoader.load((getClass().getResource("/sendMail.fxml")));
        Scene scene = new Scene(root,600,400);
        Stage stage =new Stage();
        stage.setTitle("INVIO MAIL");
        stage.setScene(scene);
        stage.show();
    }

    public static Client getClient() {
        return client;
    }

}
