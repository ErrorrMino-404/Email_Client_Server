package controller;

import app.ReplyMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Client;
import var.Email;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReplyController implements Initializable {
    @FXML
    private Label destLabel;
    @FXML
    private Label dest;
    @FXML
    private Label mit;
    @FXML
    private Label text;
    @FXML
    private Label arg;
    @FXML
    private TextArea rep;

    private Client c;
    private String user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        c = ReplyMail.getClient();
        arg.setText(ReplyMail.getArg());
        text.setText(ReplyMail.getText());

        if(Objects.equals(ReplyMail.getOth(), "")){
            System.out.println("non esiste nulla");
            mit.setText(ReplyMail.getMit());
            destLabel.setVisible(false);
        }else {
            destLabel.setVisible(false);
            String[] destinatari = ReplyMail.getOth().split(",");
            mit.setText(destOther(destinatari, c.getUser()));
        }

    }
    public void handleSend(ActionEvent actionEvent) throws IOException {
        if(Objects.equals(ReplyMail.getOth(), "")) {
            Email e = new Email(0, c.getUser(), mit.getText(), arg.getText(), rep.getText(),
                    Calendar.getInstance().getTime().toString(), mit.getText());
            c.sendTxt(e);
        }else{
            String[] destinatari = ReplyMail.getOth().split(",");
            int i=0;
            for (String d : destinatari) {
                if(Objects.equals(d, c.getUser())){
                    d = ReplyMail.getMit();
                    System.out.println(d);
                }
                Email e = new Email(0, c.getUser(), d, arg.getText(),rep.getText(),
                        Calendar.getInstance().getTime().toString(),destOther(destinatari,c.getUser()));
                c.sendTxt(e);
            }
        }
        mit.getScene().getWindow().hide();
    }

    private String destOther(String[] destinatari,String dest_email){
        String res = "";
        int i=0;
            for(String d:destinatari){
                if(Objects.equals(d, dest_email)){
                    res = res + ReplyMail.getMit();;
                }else{
                    res=res+d;
                }
                i++;
                if(i<destinatari.length){
                    res=res+",";
                }
            }
            System.out.println(res);
        return res;
    }
}
