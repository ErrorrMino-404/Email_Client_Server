package controller;

import app.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import model.Client;
import var.Email;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button forward;
    @FXML
    private Button add;
    @FXML
    private Button reply;
    @FXML
    private Button rem;
    @FXML
    private Button reply_all;
    @FXML
    private Label mittente;
    @FXML
    private Label destinatario;
    @FXML
    private Label argomento;
    @FXML
    private Label textMail;
    @FXML
    private Label user;
    @FXML
    private Label date;
    @FXML
    private ListView<Email> listMailInv;
    @FXML
    private ListView<Email> listMailRec;
    @FXML
    private ListView<String> listNotify;
    @FXML
    private TitledPane notify;

    private Client client;
    private Email selectedMail;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String userMail = ClientApp.getUser();
        client = ClientApp.getClient();
        user.setText(userMail);
        client.generateEmail();
        reply.setVisible(false);
        reply_all.setVisible(false);
        forward.setVisible(false);
        listMailInv.setItems(client.getListInv());/*faccio un legamento tra i due */
        listMailInv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Email mail;
                mail = listMailInv.getSelectionModel().getSelectedItem();
                selectedMail = mail;
                updateTestEmail(mail);
                reply.setVisible(false);
                reply_all.setVisible(false);
                forward.setVisible(false);
            }
        });
        listMailRec.itemsProperty().bind(client.getListRec());
        listMailRec.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Email mail;
                mail = listMailRec.getSelectionModel().getSelectedItem();
                selectedMail = mail;
                updateTestEmail(mail);
                reply.setVisible(true);
                String[] s = mail.getOther_dest().split(",");
                forward.setVisible(true);
                if(s.length>1){
                    reply_all.setVisible(true);
                }else{
                    reply_all.setVisible(false);
                }
            }
        });
        System.out.println("inizializzo");
        listNotify.itemsProperty().bind(this.client.getNotification());
        notify.setExpanded(false);
        listNotify.itemsProperty().addListener(event->notify.setExpanded(true));
    }


    public void updateTestEmail(Email mail){
        if(mail!=null) {
            mittente.setText(mail.getMit());
            destinatario.setText(mail.getDest());
            argomento.setText(mail.getArg());
            date.setText(mail.getDate());
            textMail.setText(mail.getText());
        }
    }
    @FXML
    private void handleAddEmail(ActionEvent actionEvent) throws IOException {
        SendMail s = new SendMail(client);
        System.out.println(Thread.currentThread().getName());
    }
    @FXML
    private void handleReplyEmail(ActionEvent actionEvent) throws IOException {
        ReplyMail r = new ReplyMail(mittente.getText(),argomento.getText(),textMail.getText(),client,"");

    }
    @FXML
    private void handleRemoveEmail(ActionEvent actionEvent) {
        if (client.getListEmailInv().contains(this.selectedMail)){
            client.removeInv(this.selectedMail);
        }else if(client.getListEmailRec().contains(this.selectedMail)){
            client.removeRec(this.selectedMail);
        }
        System.out.println("rimosso");
    }
    @FXML
    private void handleReplyAllEmail(ActionEvent actionEvent) throws IOException {
        ReplyMail r = new ReplyMail(mittente.getText(),argomento.getText(),textMail.getText(),client,selectedMail.getOther_dest());
        System.out.println("messaggio a tutti");
    }
    @FXML
    private void handleForwardEmail(ActionEvent actionEvent) throws IOException {
        ForwardMail s = new ForwardMail(client,textMail.getText(),mittente.getText());
    }
}
