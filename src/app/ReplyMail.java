package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;

import java.io.IOException;
import java.util.Objects;

public class ReplyMail {
    private static String m;
    private static String a;
    private static String t;
    private static Client c;
    private static String oth;
    public ReplyMail(String mit,String arg,String text,Client cli,String other_dest) throws IOException {
        m=mit;
        a=arg;
        t=text;
        c=cli;
        oth=other_dest;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/replyMail.fxml")));
        Scene scene = new Scene(root,600,450);
        Stage stage =new Stage();
        stage.setTitle("RISPOSTA");
        stage.setScene(scene);
        stage.show();
    }
    public static String getMit(){return m;}
    public static String getArg(){return a;}
    public static String getText(){return t;}
    public static Client getClient(){return c;}
    public static String getOth(){return oth;}

}
