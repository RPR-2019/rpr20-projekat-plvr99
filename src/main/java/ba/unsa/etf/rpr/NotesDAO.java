package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class NotesDAO {
    private static NotesDAO instance = null;
    private Connection conn = null;

    public static NotesDAO getInstance(){
        if (instance == null) instance = new NotesDAO();
        return instance;
    }
    public NotesDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:biljeske_db.db");
            PreparedStatement p = conn.prepareStatement("Select  * from users" );
        } catch (SQLException throwables) {
            renewDatabase();
            throwables.printStackTrace();
        }
    }
    private void renewDatabase() {
        Scanner input = null;
        try {
            input = new Scanner(new FileInputStream("biljeske_db.sql"));
            String sqlQuery = "";
            while (input.hasNext()) {
                sqlQuery += input.nextLine();
                if (sqlQuery.length() > 1 && sqlQuery.charAt(sqlQuery.length() - 1) == ';') {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlQuery);
                        sqlQuery = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ne postoji SQL datoteka… nastavljam sa praznom bazom");
        }
    }

    public int lastIdOfTable(String tableName){
        try {
            String upit = "Select max(id) from " + tableName;
            PreparedStatement p = conn.prepareStatement(upit);
            ResultSet resultSet = p.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public boolean userCheck(String username, String password){
        try {
            PreparedStatement check = conn.prepareStatement(
                    "Select count(*) from users k where k.username=? and k.password=?;");
            check.setString(1, username);
            check.setString(2, password);
            ResultSet resultSet = check.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void userInsert(String username, String password, String firstName, String lastName){
        try {
            PreparedStatement provjera = conn.prepareStatement(
                    "Insert into users(id,username,password,firstName,lastName) " +
                            "values (" + lastIdOfTable("users")+1 + ",'" + username + "', '"+password+"', '" + firstName+"', '"+lastName+"');");
           provjera.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean usernameInDBCheck(String username) {
        try{
            PreparedStatement provjera = conn.prepareStatement("Select count(*) from users k where k.username =?;");
            provjera.setString(1, username);
            ResultSet resultSet = provjera.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean subjectInDBCheck(String subject){
        try{
            PreparedStatement provjera = conn.prepareStatement("Select count(*) from subject p where p.naziv =?;");
            provjera.setString(1, subject);
            ResultSet resultSet = provjera.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void subjectInsert(String subject) {
        try{
            Statement upit = conn.createStatement();
            upit.executeUpdate(
                    "Insert into subject(id,naziv) values(" +(lastIdOfTable("subject")+1)+", '"+subject+"');");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public User getUser(String username) {
        try{
            PreparedStatement statement = conn.prepareStatement("Select k.id, k.username from users k where k.username=?;");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            User user = new User(resultSet.getInt(1), resultSet.getString(2));
            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void updateNoteFavorite(Note note, Boolean favorite) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("UPDATE note set favorite = ? where id=?;");
            upit.setInt(1, favorite ? 1 : 0);
            upit.setInt(2, note.getId());
            upit.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Subject> allSubjects() {
        ArrayList subjects = new ArrayList<Subject>();
        try{
            Statement upit = conn.createStatement();
            ResultSet rs = upit.executeQuery("SELECT * from subject;");
            while (rs.next()){
                Subject p = new Subject(rs.getInt(1), rs.getString(2));
                subjects.add(p);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjects;
    }

    public ArrayList<Note> allNotes(User user) {
        ArrayList notes = new ArrayList<Note>();
        try{
            PreparedStatement statement = conn.prepareStatement("SELECT * from note where korisnikId=?;");
            statement.setInt(1, user.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                Note tmp = new Note(rs.getInt(1),
                        rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7),
                        rs.getInt(8)==1);
                notes.add(tmp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return notes;
    }

    public boolean noteInDBCheck(String name, User user) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("SELECT count(*) from note b where b.name=? and b.korisnikId=?;");
            upit.setString(1,name);
            upit.setInt(2, user.getId());
            ResultSet rs = upit.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public Note getNote(String note) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("SELECT b.id, b.predmetId, b.korisnikId, b.name, b.textBiljeske, b.dateCreated, b.lastModified" +
                            " from note b where b.naziv=?");
            upit.setString(1,note);
            ResultSet rs = upit.executeQuery();
            rs.next();
            return new Note(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7), rs.getInt(8)==1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void updateNote(String naziv, String text, User user) {
        try{
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            String lastModified = dateFormat.format(LocalDateTime.now());
            PreparedStatement statement = conn.prepareStatement("UPDATE note set textBiljeske=?, lastModified=? where name=? and korisnikId=?;");
            statement.setString(1,text);
            statement.setString(2, lastModified);
            statement.setString(3, naziv);
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }

    public void noteInsert(Note note) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("Insert into note(id,predmetId,korisnikId,name,textBiljeske,dateCreated,lastModified,favorite) " +
                    "values(?,?,?,?,?,?,?,?);");
            note.setId(lastIdOfTable("note")+1);
            upit.setInt(1, note.getId());
            upit.setInt(2, note.getPredmetId());
            upit.setInt(3, note.getKorisnikId());
            upit.setString(4, note.getNaziv());
            upit.setString(5, note.getText());
            upit.setString(6, note.getDateCreated());
            upit.setString(7, note.getLastModified());
            upit.setInt(8, note.isFavorite()? 1 : 0);

            upit.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Note> filterNotes(String name, Subject subject, String date, boolean selected, User user) {
        StringBuilder builder = new StringBuilder("Select * from note b where ");
        if(name != null){
            builder.append("b.name like '%").append(name).append("%'");
            if(selected || subject !=null || date!= null ) builder.append(" and ");
        }
        if(subject != null){
            builder.append("b.predmetId=").append(subject.getId());
            if(selected || date!= null) builder.append(" and ");
        }
        if(date != null){
            builder.append("b.dateCreated like '").append(date).append("%'");
            if(selected) builder.append(" and ");
        }
        if(selected) builder.append("favorite=1");
        builder.append(" and b.korisnikId=").append(user.getId()).append(";");
        ArrayList<Note> notes = new ArrayList<Note>();
        try{
            Statement upit = conn.createStatement();
            System.out.println(builder.toString());
            ResultSet rs = upit.executeQuery(builder.toString());
            while (rs.next()){
                Note b = new Note(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getInt(8)==1);
                notes.add(b);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return notes;
    }

    public void noteRemove(int noteId) {
        try{
            PreparedStatement statement =
                    conn.prepareStatement("DELETE FROM note where id=?;");
            statement.setInt(1,noteId);
            statement.executeUpdate();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
}
