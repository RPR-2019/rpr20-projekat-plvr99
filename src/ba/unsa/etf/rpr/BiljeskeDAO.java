package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class BiljeskeDAO {
    private static BiljeskeDAO instance = null;
    private Connection conn = null;

    public static  BiljeskeDAO getInstance(){
        if (instance == null) instance = new BiljeskeDAO();
        return instance;
    }
    public BiljeskeDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:biljeske_db.db");
            PreparedStatement p = conn.prepareStatement("Select  * from korisnik" );
        } catch (SQLException throwables) {
            regenerisiBazu();
            throwables.printStackTrace();
        }
    }
    private void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("biljeske_db.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if (sqlUpit.length() > 1 && sqlUpit.charAt(sqlUpit.length() - 1) == ';') {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ne postoji SQL datoteka… nastavljam sa praznom bazom");
        }
    }

    public int zadnjiIdTabele(String nazivTabele){
        try {
            String upit = "Select max(id) from " + nazivTabele;
            PreparedStatement p = conn.prepareStatement(upit);
            ResultSet resultSet = p.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public boolean korisnikProvjera(String username, String password){
        try {
            PreparedStatement provjera = conn.prepareStatement(
                    "Select count(*) from korisnik k where k.username=? and k.password=?;");
            provjera.setString(1, username);
            provjera.setString(2, password);
            ResultSet resultSet = provjera.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void korisnikInsert(String username, String password, String firstName, String lastName){
        // TODO: 3.7.2021 Uraditi provjeru prije inserta
        try {
            PreparedStatement provjera = conn.prepareStatement(
                    "Insert into korisnik(id,username,password,firstName,lastName) " +
                            "values (" + zadnjiIdTabele("korisnik")+1 + ",'" + username + "', '"+password+"', '" + firstName+"', '"+lastName+"');");
           provjera.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean usernameUBaziCheck(String username) {
        try{
            PreparedStatement provjera = conn.prepareStatement("Select count(*) from korisnik k where k.username =?;");
            provjera.setString(1, username);
            ResultSet resultSet = provjera.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean predmetUBaziCheck(String predmet){
        try{
            PreparedStatement provjera = conn.prepareStatement("Select count(*) from predmet p where p.naziv =?;");
            provjera.setString(1, predmet);
            ResultSet resultSet = provjera.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void predmetInsert(String predmet) {
        try{
            Statement upit = conn.createStatement();
            upit.executeUpdate(
                    "Insert into predmet(id,naziv) values(" +(zadnjiIdTabele("predmet")+1)+", '"+predmet+"');");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Korisnik getKorisnik(String username) {
        try{
            PreparedStatement statement = conn.prepareStatement("Select k.id, k.username from korisnik k where k.username=?;");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Korisnik korisnik = new Korisnik(resultSet.getInt(1), resultSet.getString(2));
            return korisnik;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void updateBiljeskaFavorite(Biljeska biljeska, Boolean favorite) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("UPDATE biljeska set favorite = ? where id=?;");
            upit.setInt(1, favorite ? 1 : 0);
            upit.setInt(2, biljeska.getId());
            upit.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Predmet> sviPredmeti() {
        ArrayList predmets = new ArrayList<Predmet>();
        try{
            Statement upit = conn.createStatement();
            ResultSet rs = upit.executeQuery("SELECT * from predmet;");
            while (rs.next()){
                Predmet p = new Predmet(rs.getInt(1), rs.getString(2));
                predmets.add(p);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return predmets;
    }

    public ArrayList<Biljeska> sveBiljeske(Korisnik korisnik) {
        ArrayList biljeske = new ArrayList<Biljeska>();
        try{
            PreparedStatement upit = conn.prepareStatement("SELECT * from biljeska where korisnikId=?;");
            upit.setInt(1, korisnik.getId());
            ResultSet rs = upit.executeQuery();
            while (rs.next()){
                Biljeska tmp = new Biljeska(rs.getInt(1),
                        rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7),
                        rs.getInt(8)==1);
                biljeske.add(tmp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return biljeske;
    }

    public boolean biljeskaUBaziCheck(String naziv, Korisnik korisnik) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("SELECT count(*) from biljeska b where b.name=? and b.korisnikId=?;");
            upit.setString(1,naziv);
            upit.setInt(2, korisnik.getId());
            ResultSet rs = upit.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public Biljeska getBiljeska(String naziv) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("SELECT b.id, b.predmetId, b.korisnikId, b.name, b.textBiljeske, b.dateCreated, b.lastModified" +
                            " from biljeska b where b.naziv=?");
            upit.setString(1,naziv);
            ResultSet rs = upit.executeQuery();
            rs.next();
            return new Biljeska(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7), rs.getInt(8)==1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void updateBiljeska(String naziv, String text, Korisnik korisnik) {
        try{
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            String lastModified = dateFormat.format(LocalDateTime.now());
            PreparedStatement statement = conn.prepareStatement("UPDATE biljeska set textBiljeske=?, lastModified=? where name=? and korisnikId=?;");
            statement.setString(1,text);
            statement.setString(2, lastModified);
            statement.setString(3, naziv);
            statement.setInt(4, korisnik.getId());
            statement.executeUpdate();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }

    public void biljeskaInsert(Biljeska biljeska) {
        try{
            PreparedStatement upit =
                    conn.prepareStatement("Insert into biljeska(id,predmetId,korisnikId,name,textBiljeske,dateCreated,lastModified,favorite) " +
                    "values(?,?,?,?,?,?,?,?);");
            biljeska.setId(zadnjiIdTabele("biljeska")+1);
            upit.setInt(1, biljeska.getId());
            upit.setInt(2,biljeska.getPredmetId());
            upit.setInt(3,biljeska.getKorisnikId());
            upit.setString(4, biljeska.getNaziv());
            upit.setString(5, biljeska.getText());
            upit.setString(6, biljeska.getDateCreated());
            upit.setString(7, biljeska.getLastModified());
            upit.setInt(8, biljeska.isFavorite()? 1 : 0);

            upit.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Biljeska> filterBiljeske(String naziv, Predmet predmet, String datum, boolean selected, Korisnik korisnik) {
        StringBuilder builder = new StringBuilder("Select * from biljeska b where ");
        if(naziv != null){
            builder.append("b.name like '%").append(naziv).append("%'");
            if(selected || predmet!=null || datum!= null ) builder.append(" and ");
        }
        if(predmet != null){
            builder.append("b.predmetId=").append(predmet.getId());
            if(selected || datum!= null) builder.append(" and ");
        }
        if(datum != null){
            builder.append("b.dateCreated like '").append(datum).append("%'");
            if(selected) builder.append(" and ");
        }
        if(selected) builder.append("favorite=1");
        builder.append(" and b.korisnikId=").append(korisnik.getId()).append(";");
        ArrayList<Biljeska> biljeske = new ArrayList<Biljeska>();
        try{
            Statement upit = conn.createStatement();
            System.out.println(builder.toString());
            ResultSet rs = upit.executeQuery(builder.toString());
            while (rs.next()){
                Biljeska b = new Biljeska(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getInt(8)==1);
                biljeske.add(b);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return biljeske;
    }

    public void biljeskaRemove(int biljeskaId) {
        try{
            PreparedStatement statement =
                    conn.prepareStatement("DELETE FROM biljeska where id=?;");
            statement.setInt(1,biljeskaId);
            statement.executeUpdate();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
}
