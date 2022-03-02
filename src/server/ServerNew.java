package server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import var.Email;
import var.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ServerNew {
    private final Server server;
    private Map<String, ReentrantReadWriteLock> usersUtil;
    private Map<String, ArrayList<Email>> listMailUser;
    private final ObservableList<Log> logs;

    public ServerNew() {
        logs = FXCollections.observableArrayList();
        logs.add(0,new Log("Avvio del server"));
        buildUsersUtil();
        this.server = new Server();
        server.setDaemon(true);
        server.start();
    }

    private void buildUsersUtil(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path= Objects.requireNonNull(loader.getResource("./mails/")).getPath();
        File[] listOfFiles = new File(path).listFiles();
        assert listOfFiles !=null;
        usersUtil = new HashMap<>();
        listMailUser = new HashMap<>();
        for(File f:listOfFiles){
            String user = f.getName().substring(0,f.getName().length()-4);
            System.out.println(user);
            usersUtil.put(user,new ReentrantReadWriteLock());
            listMailUser.put(user,new ArrayList<>());
            buildListEmail(user);
        }
    }
    private void buildListEmail(String user){
        Lock rlock = usersUtil.get(user).readLock();
        rlock.lock();
        try{
            FileReader f = new FileReader("mails/"+user+".txt");
            BufferedReader b = new BufferedReader(f);
            String s=b.readLine();
            while(s!=null){
                String[] split = s.split("\\|");
                Email email = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4],split[5],split[6]);
                listMailUser.get(user).add(email);
                s=b.readLine();
            }
        }catch(Exception e){
            System.out.println(e);
        }
        rlock.unlock();
    }
    private boolean checkNewEmail(String user){
        Lock rlock= usersUtil.get(user).readLock();
        rlock.lock();
        boolean res = false;
        try {
            FileReader r = new FileReader("mails/"+user+".txt");
            BufferedReader b = new BufferedReader(r);
            int x = (int)b.lines().count();
            r = new FileReader("mails/"+user+".txt");
            b = new BufferedReader(r);
            for(int i=0;i<x;i++){
                String s = b.readLine();
                String[] mail= s.split("\\|");
                if(Objects.equals(mail[0],"1")){
                    System.out.println("nuovo messaggio");
                    res=true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rlock.unlock();
        return res;
    }
    private Email newMail(String user){
        Lock rlock= usersUtil.get(user).readLock();
        rlock.lock();
        listMailUser.get(user).removeIf(email -> email.getID() == 1);
        Email e=null ;
        try {
            FileReader r = new FileReader("mails/"+user+".txt");
            BufferedReader b = new BufferedReader(r);
            int x = (int)b.lines().count();
            r = new FileReader("mails/"+user+".txt");
            b = new BufferedReader(r);
            for(int i=0;i<x;i++){
                String s = b.readLine();
                String[] split = s.split("\\|");
                if(Objects.equals(split[0],"1") ) {
                    e = new Email(0, split[1], split[2], split[3], split[4],split[5],split[6]);
                    listMailUser.get(user).add(e);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        rlock.unlock();
        return e;
    }
    private void newAddEmail(String user){
        Lock wlock = usersUtil.get(user).writeLock();
        wlock.lock();
        System.out.println(listMailUser.get(user).size());
        try {
            FileWriter f = new FileWriter("mails/"+user+".txt");
            for(Email e: listMailUser.get(user)){
                FileWriter fx = new FileWriter("mails/"+ user+".txt",true);
                fx.write(0+"|"+e.getMit()+"|"+e.getDest()+"|"+e.getArg()+"|"
                        +e.getText()+"|"+e.getDate()+"|"+e.getOther_dest());
                fx.write("\n");
                fx.flush();
            }
        }catch(Exception ex){
            System.out.println("problema in newAddEmail "+ ex);
        }
        wlock.unlock();
    }
    private boolean sendMail(Email e,String user){
        Lock wlock = usersUtil.get(user).readLock();
        wlock.lock();
        boolean res = true;
        try{
            FileWriter w = new FileWriter("mails/"+e.getDest()+".txt",true);
            w.write(1+"|"+e.getMit()+
                    "|"+e.getDest()+"|"+e.getArg()+"|"+e.getText()+"|"+e.getDate()+"|"+e.getOther_dest());
            w.write("\n");
            w.flush();
            listMailUser.get(user).add(e);
            wlock.unlock();
            return res;
        }catch(Exception ex){
            System.out.println("file non trovato");
            res=false;
            wlock.unlock();
            return res;
        }
    }
    private boolean removeEmail(String user,Email email) {
        Lock wlock = usersUtil.get(user).writeLock();
        wlock.lock();
        System.out.println("rimuovo il dato");
        System.out.println(listMailUser.get(user).contains(email));
        listMailUser.get(user).remove(email);

        boolean res = false;
        try{
            FileWriter f=new FileWriter("mails/"+user+".txt");
            for(Email e: listMailUser.get(user)){
                FileWriter fx = new FileWriter("mails/"+user+".txt",true);
                if(Objects.equals(e.getDate(), email.getDate()) &&
                        Objects.equals(e.getArg(), email.getArg()) &&
                        Objects.equals(e.getText(), email.getText()) &&
                        Objects.equals(e.getMit(), email.getMit()) &&
                        Objects.equals(e.getDest(), email.getDest())) {
                        listMailUser.get(user).remove(e);
                }else {
                    fx.write(e.getID() + "|" + e.getMit() + "|" + e.getDest() + "|" + e.getArg() + "|"
                            + e.getText() + "|" + e.getDate() + "|" + e.getOther_dest());
                    fx.write("\n");
                    fx.flush();
                }
            }
            res = true;
        }catch(IOException e){
            System.out.println("Rimozione mail non avvenuta"+e);
        }
        wlock.unlock();
        return res;
    }

    class Server extends Thread {
        private ServerSocket serverSocket;
        private ExecutorService exec;

        @Override
        public void run() {
            exec = null;
            try {
                exec = Executors.newSingleThreadExecutor();
                int port = 51560;
                serverSocket = new ServerSocket(port);
                while (true) {
                    Socket incoming = serverSocket.accept();
                    exec.execute(new Request(incoming));
                }
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            } finally {
                try {
                    if (serverSocket != null)
                        serverSocket.close();
                    if (exec != null && !exec.isShutdown())
                        ServerNew.this.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        class Request implements Runnable {
            Socket clientSocket;
            ServerNew serv;

            public Request(Socket socket) {
                clientSocket = socket;
                serv = ServerNew.this;
            }
            public void run() {
                Message message;
                ObjectInputStream inputRequest=null;
                try {
                    inputRequest = new ObjectInputStream(clientSocket.getInputStream());
                    Object msg = inputRequest.readObject();
                    if (msg instanceof Message) {
                        message = (Message) msg;
                    } else {
                        return;
                    }
                    Object o = null;
                    short status = Message.ERROR;
                    String user;
                    String logMsg=null;
                    switch (message.getOperation()) {
                        case Message.SEND_NEW_EMAIL -> {
                            user = ((Email) message.getObj()).getMit();
                            File f = new File("mails/" + ((Email) message.getObj()).getDest() + ".txt");
                            if (f.isFile()) {
                                if (sendMail((Email) message.getObj(), user)) {
                                    status = Message.SUCCESS;
                                    o = (Email) message.getObj();
                                    logMsg = "Inviato correttamente";
                                } else {
                                    o = "Errore nell'invio del messaggio";
                                    logMsg = "Errore invio del messaggio"+(Email) message.getObj();
                                }
                            } else {
                                removeEmail(user,(Email)message.getObj());
                                o = "Indirizzo non esistente" + ((Email) message.getObj()).getDest();
                                logMsg = "Indirizzo non esistente"+((Email) message.getObj()).getDest();
                            }
                        }
                        case Message.REMOVE_EMAIL_REC -> {
                            user = (((Email) message.getObj()).getDest());
                            if (removeEmail(user, (Email) message.getObj())) {
                                status = Message.SUCCESS;
                                o = "rimozione successo";
                                logMsg="rimozione avvenuta";

                            } else {
                                logMsg="Errore rimozione";
                                o = "Errore nella rimozione";
                            }
                        }
                        case Message.REMOVE_EMAIL_SEND -> {
                            user = (((Email) message.getObj()).getMit());
                            if (removeEmail(user, (Email) message.getObj())) {
                                status = Message.SUCCESS;
                                o = "rimozione";
                                logMsg="rimozione avvenuta";

                            } else {
                                logMsg="Errore rimozione";
                                o = "Errore nella rimozione";
                            }
                        }
                        case Message.CHECK_NEW -> {
                            user = (String) message.getObj();
                            if (checkNewEmail(user)) {
                                Email email = newMail(user);
                                System.out.println(email);
                                newAddEmail(user);
                                status = Message.SUCCESS;
                                o = Objects.requireNonNull(email);
                                logMsg="Nuovo messaggio ";
                            } else {
                                o = "Nessuna mail nuova";
                                logMsg="Nessuna mail";
                            }
                        }
                        default -> o = null;
                    }
                    sendResponse(status, o);
                    logs.add(0,new Log(logMsg));
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                } finally {
                    try {
                        if (clientSocket != null)
                            clientSocket.close();
                        if (inputRequest != null)
                            inputRequest.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            private void sendResponse(short status, Object o) {
                ObjectOutputStream outputRequest = null;
                try {
                    Message m = new Message(status,o);
                    outputRequest = new ObjectOutputStream(clientSocket.getOutputStream());
                    outputRequest.writeObject(m);
                    outputRequest.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputRequest != null)
                            outputRequest.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void setON(){

        Server serverN= new Server();
        serverN.start();
        String logMsg="Avvio del server";
        logs.add(0,new Log(logMsg));

    }
    public void setOFF() {
       try{
           shutdown();
           server.serverSocket.close();
           String logMsg="Chiusura del server";
           logs.add(0,new Log(logMsg));
       }catch(IOException e){
           System.out.println(e+" errore chiusura");
       }
    }
    public void shutdown() {
        server.exec.shutdown();
        try {
            if (!server.exec.awaitTermination(60, TimeUnit.SECONDS)) {
                server.exec.shutdownNow();
                if (!server.exec.awaitTermination(60, TimeUnit.SECONDS))
                    System.out.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            server.exec.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ObservableList<Log> getLogs(){
        return logs;
    }
    class Log{
        private final String logEvent;
        private final String logTime;

        public Log(String event){
            Date cal = Calendar.getInstance().getTime();
            SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
            logTime = s.format(cal);
            logEvent = event;
        }
        public String getLogEvent(){

            return logEvent;
        }
        public String getLogTime(){
            return logTime;
        }
    }
}
