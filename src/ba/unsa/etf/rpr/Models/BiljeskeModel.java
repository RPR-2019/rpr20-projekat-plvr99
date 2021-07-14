package ba.unsa.etf.rpr.Models;

import ba.unsa.etf.rpr.Biljeska;
import ba.unsa.etf.rpr.BiljeskeDAO;
import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Predmet;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class BiljeskeModel {
    private static BiljeskeModel instance = null;
    public static BiljeskeDAO dao;
    public ObservableList<Predmet> predmeti;
    public ObservableList<Biljeska> biljeske;
    private Korisnik korisnik;
    public BiljeskeModel(Korisnik korisnik) {
        this.korisnik = korisnik;
        dao = BiljeskeDAO.getInstance();
        this.biljeske = FXCollections.observableArrayList();
        this.predmeti = FXCollections.observableArrayList();
    }
    public static BiljeskeModel getModelInstance(Korisnik korisnik){
        if (instance == null) instance = new BiljeskeModel(korisnik);
        return instance;
    }
    public void napuniModelPodacima(){
        sviPredmeti();
        sveBiljeske();
    }

    public ObservableList<Predmet> getPredmeti() {
        return predmeti;
    }

    public ObservableList<Biljeska> getBiljeske() {
        return biljeske;
    }

    public boolean predmetUBaziCheck(String predmet){
        return dao.predmetUBaziCheck(predmet);
    }

    public void predmetInsert(String predmet) {
        new Thread(()->{
            dao.predmetInsert(predmet);
        }).start();
    }

    public void sviPredmeti(){
        predmeti.addAll(dao.sviPredmeti());
    }

    public void updateBiljeskaFavorite(Biljeska biljeska, Boolean favorite) {
        new Thread(()->{
            dao.updateBiljeskaFavorite(biljeska, favorite);
        }).start();
    }

    public void insertBiljeska(int predmetId, String name, String text){
        new Thread(()->{
            Biljeska insert = new Biljeska(-1, predmetId, korisnik.getId(), name, text);
            dao.biljeskaInsert(insert);
        }).start();

    }

    public void sveBiljeske() {
        biljeske.addAll( dao.sveBiljeske(korisnik) );
    }

    public boolean biljeskaUBaziCheck(String naziv) {
        return dao.biljeskaUBaziCheck(naziv, korisnik);
    }

    public void updateBiljeska(String naziv, String text) {
        new Thread(()->{
            dao.updateBiljeska(naziv, text, korisnik);
        }).start();
    }

    public void filterBiljeske(String naziv, Predmet predmet, String datum, boolean selected) {
        if(datum != null) {
            datum= datum.replaceAll(" ","");
            if (datum.charAt(1) == '.') datum = "0" + datum;
            if (datum.charAt(4) == '.') datum = datum.substring(0, 3) + "0" + datum.substring(3);
            datum = datum.substring(0,6) + datum.substring(8,10);
            System.out.println();
        }
        biljeske.addAll( dao.filterBiljeske(naziv, predmet, datum, selected, korisnik) );
    }

    public void biljeskaRemove(Biljeska biljeska) {
        biljeske.remove(biljeska);
        new Thread(()->{
            dao.biljeskaRemove(biljeska.getId());
        }).start();
    }

    public void clearBiljeske() {
        biljeske.clear();
    }
    public void clearPredmeti(){
        predmeti.clear();
    }

    public void exportFile(File selectedFile, String text) {
        new Thread(() ->{
            try {
                HtmlConverter.convertToPdf(text, new PdfWriter(selectedFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
