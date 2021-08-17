package ba.unsa.etf.rpr.Models;

import ba.unsa.etf.rpr.Note;
import ba.unsa.etf.rpr.NotesDAO;
import ba.unsa.etf.rpr.Subject;
import ba.unsa.etf.rpr.User;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class NotesModel {
    private static NotesModel instance = null;
    public static NotesDAO dao;
    public ObservableList<Subject> subjects;
    public ObservableList<Note> notes;
    private User user;
    public NotesModel(User user) {
        this.user = user;
        dao = NotesDAO.getInstance();
        this.notes = FXCollections.observableArrayList();
        this.subjects = FXCollections.observableArrayList();
    }
    public static NotesModel getModelInstance(User user){
        if (instance == null) instance = new NotesModel(user);
        return instance;
    }
    public void fillModelWData(){
        subjects.clear();
        notes.clear();
        allSubjects();
        allNotes();
    }

    public ObservableList<Subject> getSubjects() {
        return subjects;
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public boolean subjectInDBCheck(String subject){
        return dao.subjectInDBCheck(subject);
    }

    public void subjectInsert(String predmet) {
        new Thread(()->{
            dao.subjectInsert(predmet);
        }).start();
    }

    public void allSubjects(){
        subjects.addAll(dao.allSubjects());
    }

    public void updateNoteFavorite(Note note, Boolean favorite) {
        new Thread(()->{
            dao.updateNoteFavorite(note, favorite);
        }).start();
    }

    public void noteInsert(int subjectId, String name, String text){
        new Thread(()->{
            Note insert = new Note(-1, subjectId, user.getId(), name, text);
            dao.noteInsert(insert);
        }).start();

    }

    public void allNotes() {
        notes.addAll( dao.allNotes(user) );
    }

    public boolean noteInDBCheck(String naziv) {
        return dao.noteInDBCheck(naziv, user);
    }

    public void updateNote(String naziv, String text) {
        new Thread(()->{
            dao.updateNote(naziv, text, user);
        }).start();
    }

    public void filterNotes(String naziv, Subject subject, String datum, boolean selected) {
        if(datum != null) {
            datum= datum.replaceAll(" ","");
            if (datum.charAt(1) == '.') datum = "0" + datum;
            if (datum.charAt(4) == '.') datum = datum.substring(0, 3) + "0" + datum.substring(3);
            datum = datum.substring(0,6) + datum.substring(8,10);
        }
        notes.addAll( dao.filterNotes(naziv, subject, datum, selected, user) );
    }

    public void noteRemove(Note note) {
        notes.remove(note);
        new Thread(()->{
            dao.noteRemove(note.getId());
        }).start();
    }

    public void clearNotes() {
        notes.clear();
    }
    public void clearSubjects(){
        subjects.clear();
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
