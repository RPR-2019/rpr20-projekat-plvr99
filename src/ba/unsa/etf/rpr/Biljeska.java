package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Biljeska {
    private int id;
    private int predmetId;
    private int korisnikId;
    private SimpleStringProperty naziv;
    private SimpleStringProperty text;
    private final String dateCreated;
    private SimpleStringProperty lastModified;
    private boolean favorite;

    public Biljeska(int id, int predmetId, int korisnikId, String naziv, String text) {
        this.id = id;
        this.predmetId = predmetId;
        this.korisnikId = korisnikId;
        this.naziv = new SimpleStringProperty(naziv);
        this.text = new SimpleStringProperty(text);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        dateCreated = dateFormat.format(LocalDateTime.now());
        lastModified = new SimpleStringProperty(dateFormat.format(LocalDateTime.now()));
        favorite = false;
    }

    public Biljeska(int id, int predmetId, int korisnikId, String naziv, String text, String dateCreated, String lastModified, boolean favorite) {
        this.id = id;
        this.predmetId = predmetId;
        this.korisnikId = korisnikId;
        this.naziv = new SimpleStringProperty(naziv);
        this.text = new SimpleStringProperty(text);
        this.dateCreated = dateCreated;
        this.lastModified = new SimpleStringProperty(lastModified);
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPredmetId() {
        return predmetId;
    }

    public void setPredmetId(int predmetId) {
        this.predmetId = predmetId;
    }

    public int getKorisnikId() {
        return korisnikId;
    }

    public void setKorisnikId(int korisnikId) {
        this.korisnikId = korisnikId;
    }

    public String getNaziv() {
        return naziv.get();
    }

    public SimpleStringProperty nazivProperty() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public String getText() {
        return text.get();
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getLastModified() {
        return lastModified.get();
    }

    public SimpleStringProperty lastModifiedProperty() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified.set(lastModified);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
