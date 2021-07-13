package ba.unsa.etf.rpr.Models;

import ba.unsa.etf.rpr.BiljeskeDAO;
import ba.unsa.etf.rpr.Korisnik;

public class Model {
    public static BiljeskeDAO dao;

    public Model() {
        dao = BiljeskeDAO.getInstance();
    }
    public boolean korisnikProvjera(String username, String password){
        return dao.korisnikProvjera(username, password);
    }

    public boolean usernameCheck(String username){
        return dao.usernameUBaziCheck(username);
    }

    public void korisnikInsert(String username, String password, String firstName, String lastName) {
        new Thread(() -> {
            dao.korisnikInsert(username, password, firstName, lastName);
        }).start();
    }
    public Korisnik getkorisnik(String username){
        return dao.getKorisnik(username);
    }

}
