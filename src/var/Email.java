package var;


import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email implements Serializable {
    private final int ID;
    private final String mit;
    private final String dest;
    private final String arg;
    private final String text;
    private final String date;
    private final String other_dest;

    public Email(int id, String m, String d, String a, String t, String date,String other){
        this.ID=id;
        this.mit=m;
        this.dest=d;
        this.arg=a;
        this.text=t;
        this.date = date;
        this.other_dest = other;
    }

    public int getID() {return ID;}
    public String getArg() {return arg;}
    public String getDest() {return dest;}
    public String getMit() {return mit;}
    public String getText() {return text;}
    public String getDate(){return date;}
    public String getOther_dest(){return other_dest;}

    public static boolean validateEmailAddress(String email){
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
