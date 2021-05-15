public class Main {

    public static void main(String[] args){
        NoteView mNoteView = new NoteView();
        NoteViewController mNoteViewController = new NoteViewController(mNoteView);
        mNoteViewController.initController();
    }
}
