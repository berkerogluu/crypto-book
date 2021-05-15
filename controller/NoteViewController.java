import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

public class NoteViewController {
    private NoteView view;
    private ResourceBundle mBundle;
    private Properties mProperties;
    private String mPassword;

    public NoteViewController(NoteView v){
        view = v;
    }

    public void initController(){
        view.getNoteFrame().setVisible(true);
        mBundle = ResourceBundle.getBundle("Strings");

        // Preferences
        setPreferences();
        loadPreferences();

        // Button Listeners
        view.getMenuItemSave().addActionListener(e->{
            saveFile(view.getTextArea().getText());
        });

        view.getMenuItemOpen().addActionListener(e->{
            openFile();
        });

        // Shortcut Key Listeners
        view.getTextArea().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if((e.getKeyCode() == KeyEvent.VK_S) && (e.isControlDown())){
                    saveFile(view.getTextArea().getText());
                }
            }
        });

        view.getTextArea().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if((e.getKeyCode() == KeyEvent.VK_O) && (e.isControlDown())){
                    openFile();
                }
            }
        });

        view.getNoteFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(view.getNoteFrame(),
                        mBundle.getString("CloseOperation_Message"), mBundle.getString("CloseOperation_Title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    System.exit(0);
                }
            }
        });

        view.getTextArea().addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int lineNumber=0, column=0, pos=0;
                try
                {
                    pos=view.getTextArea().getCaretPosition();
                    lineNumber=view.getTextArea().getLineOfOffset(pos);
                    column=pos-view.getTextArea().getLineStartOffset(lineNumber);
                }catch(Exception mException){

                }
                if(view.getTextArea().getText().length()==0){
                    lineNumber=0; column=0;
                }
                view.getStatusBar().setText("|| "+ mBundle.getString("StatusBar_Line") + " " + (lineNumber+1)+", " + mBundle.getString("StatusBar_Col") + " " + (column+1));
            }
        });
    }

    private void setPreferences(){
        if(!Files.exists(Paths.get("preferences.xml"))){
            mProperties = new Properties();
            mProperties.setProperty("hashAlgorithm","MD5");
            mProperties.setProperty("cipherInstance", "AES/ECB/PKCS5Padding");
            mProperties.setProperty("cipherAlgorithm", "AES");
            mProperties.setProperty("startMaximized","false");

            try{
                mProperties.storeToXML(new FileOutputStream("preferences.xml"), "");
            }catch(Exception e){
                System.err.println(e.toString());
            }
        }
    }

    private void loadPreferences(){
        if(Files.exists(Paths.get("preferences.xml"))){
            mProperties = new Properties();

            try{
                mProperties.loadFromXML(new FileInputStream("preferences.xml"));
            }catch(Exception e){
                System.err.println(e.toString());
            }

            Variables.CIPHER_ALGORITHM = mProperties.getProperty("cipherAlgorithm");
            Variables.CIPHER_INSTANCE = mProperties.getProperty("cipherInstance");
            Variables.HASH_ALGORITHM = mProperties.getProperty("hashAlgorithm");
        }
    }

    private void saveFile(String i) {

        mPassword = JOptionPane.showInputDialog(view.getNoteFrame(), "Enter Password");
        if(mPassword != null){
            File file = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("Untitled-1"));
            int option = fileChooser.showSaveDialog(view.getNoteFrame());
            if(option == JFileChooser.APPROVE_OPTION){
                file = fileChooser.getSelectedFile();
                try {
                    BufferedWriter bf = new BufferedWriter(new FileWriter(file.getPath()));
                    bf.write(encryptData(i.getBytes(), hashPassword()));
                    bf.close();
                    view.getNoteFrame().setTitle(file.getName() + " - " + mBundle.getString("Frame_Title"));
                    JOptionPane.showMessageDialog(view.getNoteFrame(), "Saved Successfully!", "Done", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* System.out.println(encryptData(i.getBytes(), hashPassword()));
         System.out.println(decryptData(Base64.getDecoder().decode(encryptData(i.getBytes(), hashPassword())), hashPassword())); */
    }

    private void openFile(){
        File file = null;
        String text = "";
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(view.getNoteFrame());
        if(option == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();

            mPassword = JOptionPane.showInputDialog(view.getNoteFrame(), "Enter Password");
            if(mPassword != null){
                try{
                    BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
                    text = br.readLine();
                    br.close();

                    text = decryptData(Base64.getDecoder().decode(text.getBytes()), hashPassword());
                    view.getTextArea().setText(text);

                }catch(Exception e){
                    JOptionPane.showMessageDialog(view.getNoteFrame(), "Wrong Password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }

    }

    private byte[] hashPassword(){
        String hashedText = "";
        try {
            MessageDigest md = MessageDigest.getInstance(Variables.HASH_ALGORITHM);
            byte[] messageDigest = md.digest(mPassword.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            hashedText = no.toString(16);
            while (hashedText.length() < 32) {
                hashedText = "0" + hashedText;
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return hashedText.getBytes();
    }

    private String encryptData(byte[] data, byte[] key){
        byte[] encryptedData = null;

        try{
            Cipher mCipher = Cipher.getInstance(Variables.CIPHER_INSTANCE);
            SecretKey mKey = new SecretKeySpec(key, Variables.CIPHER_ALGORITHM);
            mCipher.init(Cipher.ENCRYPT_MODE, mKey);
            encryptedData = mCipher.doFinal(data);
            encryptedData = Base64.getEncoder().encode(encryptedData);
        }catch(Exception e){
            System.err.println(e.toString());
        }

        return new String(encryptedData);
    }

    private String decryptData(byte[] data, byte[] key){
        byte[] decryptedData = null;

        try{
            Cipher mCipher = Cipher.getInstance(Variables.CIPHER_INSTANCE);
            SecretKey mKey = new SecretKeySpec(key, Variables.CIPHER_ALGORITHM);
            mCipher.init(Cipher.DECRYPT_MODE, mKey);
            decryptedData = mCipher.doFinal(data);
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(decryptedData);
    }

}
