package ba.unsa.etf.rpr.Models;

import ba.unsa.etf.rpr.NotesDAO;
import ba.unsa.etf.rpr.User;

public class UserModel {
    public static NotesDAO dao;

    public UserModel() {
        dao = NotesDAO.getInstance();
    }
    public boolean userCheck(String username, String password){
        return dao.userCheck(username, password);
    }

    public boolean usernameCheck(String username){
        return dao.usernameInDBCheck(username);
    }

    public void userInsert(String username, String password, String firstName, String lastName) {
        new Thread(() -> {
            dao.userInsert(username, password, firstName, lastName);
        }).start();
    }
    public User getUser(String username){
        return dao.getUser(username);
    }

}
