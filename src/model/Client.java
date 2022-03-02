package model;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;

import javafx.collections.FXCollections;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import var.Email;
import var.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import java.io.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Client {
    private final String emailUser;
    private final ObservableList<Email> listEmailInv;
    private final ListProperty<Email> listInv;
    private final ObservableList<Email> listEmailRec;
    private final ListProperty<Email> listRec;
    private final SimpleListProperty<String> notify;



    public Client(String mail){
        emailUser= mail;
        listEmailInv = FXCollections.observableList(new LinkedList<>());
        listInv = new SimpleListProperty<>();
        listInv.set(listEmailInv);
        listEmailRec = FXCollections.observableList(new LinkedList<>());
        listRec = new SimpleListProperty<>();
        listRec.set(listEmailRec);
        Request rq = new Request(Message.CHECK_NEW,this.emailUser);
        rq.start();
        notify=new SimpleListProperty<>();
        notify.set(FXCollections.observableArrayList(new ArrayList<>()));
    }


    public SimpleListProperty<String> getNotification(){return notify;}
    public ObservableList<Email> getListEmailInv() {return listEmailInv;}
    public ObservableList<Email> getListEmailRec() {return listEmailRec;}

    public ListProperty<Email> getListInv() {return listInv;}
    public ListProperty<Email> getListRec() {return listRec;}

    public void removeInv(Email mail){
        listEmailInv.remove(mail);
        Request r = new Request(Message.REMOVE_EMAIL_SEND,mail);
        r.start();
    }

    public void removeRec(Email mail){
        listEmailRec.remove(mail);
        Request r = new Request(Message.REMOVE_EMAIL_REC,mail);
        r.start();
    }

    public void addRec(Email mail){
        listEmailRec.add(mail);
    }
    public String getUser(){
        return emailUser;
    }
    public Client getClient(){return this;}

    public void generateEmail(){
        try{
            FileReader r = new FileReader("mails/"+emailUser+".txt");
            BufferedReader b = new BufferedReader(r);
            int lineFile = (int) b.lines().count();
            r = new FileReader("mails/"+emailUser+".txt");
            b = new BufferedReader(r);

            for(int i = 0; i< lineFile; i++) {
                String s=b.readLine();
               if(s!=null) {
                   String[] split = s.split("\\|");
                   if(split[4].contains("\\\n")){
                       System.out.println("tovato separazione");
                   }
                   if(Objects.equals(split[1], getUser())){
                       Email email = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4],split[5],split[6]);
                           listEmailInv.add(email);

                   }else if(!Objects.equals(split[1], getUser()) && Objects.equals(split[2], getUser())){
                       Email email = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4],split[5],split[6]);
                           listEmailRec.add(email);

                   }

               }
            }
        }catch(IOException e){
            System.out.println("file non trovato \n");
        }

    }

    public void sendTxt(Email e) throws IOException {
        FileWriter f = new FileWriter("mails/"+emailUser+".txt",true);
        f.write(0+"|"+e.getMit()+
                "|"+e.getDest()+"|"+e.getArg()+"|"+e.getText()+"|"+e.getDate()+"|"+e.getOther_dest());
        f.write("\n");
        f.flush();
        Request send = new Request(Message.SEND_NEW_EMAIL,e);
        send.start();
    }

    class Request extends Thread {
        private final short operation;
        private final Object object;

        public Request(short op, Object o){
            operation = op;
            object = o;
            this.setDaemon(true);
        }
        @Override
        public void run(){
            Message m = new Message(operation,object);
            Message response;
            if(operation == Message.CHECK_NEW){
                while(true){
                    try{
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    response = communicateToServer(m);
                    assert response !=null;
                    if(response.getOperation() == Message.SUCCESS){
                        Email e= (Email)response.getObj();
                        System.out.println("nuovo messaggio"+e.getMit());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listEmailRec.add(e);
                                notify.add(0,"Nuova Email da: "+e.getMit());
                            }
                        });
                        System.out.println("Controllo effettuato");
                    }
                }
            }else{
                response = communicateToServer(m);
                assert response!=null;
                Message finalResponse = response;
                if(response.getOperation()==Message.SUCCESS){
                    switch (this.operation) {
                        case Message.SEND_NEW_EMAIL -> {
                            listEmailInv.add((Email)response.getObj());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    notify.add(0, "Inviata con successo " + ((Email) finalResponse.getObj()).getDest());
                                }
                            });
                        }
                        case Message.REMOVE_EMAIL_REC, Message.REMOVE_EMAIL_SEND -> Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                notify.add(0, "Email rimossa con successo");
                            }
                        });
                        default -> {
                        }
                    }
                    }else if(response.getOperation()==Message.ERROR){
                    switch (this.operation) {
                        case Message.SEND_NEW_EMAIL -> {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    notify.add(0,(String) finalResponse.getObj());
                                }
                            });
                        }
                        case Message.REMOVE_EMAIL_REC, Message.REMOVE_EMAIL_SEND -> Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                notify.add(0,(String) finalResponse.getObj());
                            }
                        });
                        default -> {
                        }
                    }
                    }
                }
            }
        //comunicazione verso il server socket
        private Message communicateToServer(Message toSend) {
            Socket socket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            boolean i=false;
            while (true) {
                try {
                    String host = "localhost";
                    int port = 51560;
                    socket = new Socket(host, port);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(toSend);
                    in = new ObjectInputStream(socket.getInputStream());
                    if(i) {
                        i=false;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                notify.add(0, "Server online");
                            }
                        });
                    }
                    return (Message) in.readObject();
                } catch (ConnectException e) {
                    i = true;
                    System.out.println("impossibile connettersi");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            notify.add(0, "Server offline");
                        }
                    });
                    try {
                        Thread.sleep(20000);
                    } catch (Exception ee) {
                        System.out.println("Errore qua riga client");
                        System.out.println(ee);
                    }
                } catch (Exception e) {
                        System.out.println("Errore generico nel client "+e);
                }finally
                 {
                    try {
                        if (out != null)
                            out.close();

                        if (in != null)
                            in.close();

                        if (socket != null)
                            socket.close();

                    } catch (Exception e) {
                        System.out.println("Errore chiusura stream o socket: \n" + e);
                    }
                }
            }
        }
    }
}
