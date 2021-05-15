import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class NoteView {

    // Swing Components
    private JFrame mNoteFrame;
    private JTextArea mNoteField;
    private JMenuBar mNoteMenuBar;
    private JMenu mFileMenu;
    private JMenuItem iItemOpen, iItemNew, iItemSave;
    private JMenu mEditMenu;
    private JMenuItem iItemUndo, iItemRedo, iItemFind, iItemClear;
    private JMenu mToolsMenu;
    private JMenuItem iItemAbout, iItemPreferences;
    private JScrollPane mScrollPane;
    private JLabel mStatusBar;


    // Other
    private ResourceBundle mBundle;

    public NoteView(){

        // Localize
        Locale.getDefault();
        mBundle = ResourceBundle.getBundle("Strings");

        // JMenuBar (mNoteMenuBar)
        mNoteMenuBar = new JMenuBar();

        mFileMenu = new JMenu(mBundle.getString("Menu_Button_File"));
        iItemOpen = new JMenuItem(mBundle.getString("Menu_Item_Open"));
        iItemNew = new JMenuItem(mBundle.getString("Menu_Item_New"));
        iItemSave = new JMenuItem(mBundle.getString("Menu_Item_Save"));
        mFileMenu.add(iItemOpen); mFileMenu.add(iItemNew); mFileMenu.add(new JSeparator()); mFileMenu.add(iItemSave);

        mEditMenu = new JMenu(mBundle.getString("Menu_Button_Edit"));
        iItemUndo = new JMenuItem(mBundle.getString("Menu_Item_Undo"));
        iItemRedo = new JMenuItem(mBundle.getString("Menu_Item_Redo"));
        iItemFind = new JMenuItem(mBundle.getString("Menu_Item_Find"));
        iItemClear = new JMenuItem(mBundle.getString("Menu_Item_Clear"));
        mEditMenu.add(iItemUndo); mEditMenu.add(iItemRedo); mEditMenu.add(new JSeparator()); mEditMenu.add(iItemFind); mEditMenu.add(new JSeparator()); mEditMenu.add(iItemClear);

        mToolsMenu = new JMenu(mBundle.getString("Menu_Button_Tools"));
        iItemAbout = new JMenuItem(mBundle.getString("Menu_Item_About"));
        iItemPreferences = new JMenuItem(mBundle.getString("Menu_Item_Preferences"));
        mToolsMenu.add(iItemAbout); mToolsMenu.add(new JSeparator()); mToolsMenu.add(iItemPreferences);

        mNoteMenuBar.add(mFileMenu); mNoteMenuBar.add(mEditMenu); mNoteMenuBar.add(mToolsMenu);
        // Text
        mNoteField = new JTextArea(30,60);
        mNoteField.setLineWrap(true);
        mNoteField.setWrapStyleWord(true);
        mScrollPane = new JScrollPane(mNoteField);

        // Status Bar
        mStatusBar = new JLabel("|| " + mBundle.getString("StatusBar_Line") + " 0, " + mBundle.getString("StatusBar_Col") + " 0", JLabel.RIGHT);

        // JFrame (mNoteFrame)
        mNoteFrame = new JFrame(mBundle.getString("Frame_Title"));
        mNoteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mNoteFrame.add(mScrollPane, BorderLayout.CENTER);
        mNoteFrame.add(mNoteMenuBar, BorderLayout.NORTH);
        mNoteFrame.add(mStatusBar, BorderLayout.SOUTH);
        mNoteFrame.setJMenuBar(mNoteMenuBar);

        mNoteFrame.pack();
        mNoteFrame.setSize(800,600);
        ImageIcon img = new ImageIcon(this.getClass().getResource("icon.png"));
        mNoteFrame.setIconImage(img.getImage());
    }

    public JFrame getNoteFrame() {
        return mNoteFrame;
    }
    public JTextArea getTextArea(){ return mNoteField; }
    public JLabel getStatusBar(){ return mStatusBar; }
    public JMenuItem getMenuItemSave(){ return iItemSave; }
    public JMenuItem getMenuItemOpen(){ return iItemOpen; }
}
