package aydin.firebasedemospring2024;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class Welcome {
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField signInEmailField;
    @FXML
    private PasswordField signInPasswordField;
    public boolean onRegisterBtn() {
        String password = passwordField.getText();
        System.out.println(password);

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailField.getText())
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber(phoneNumberField.getText())
                .setDisplayName(nameField.getText())
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");

            nameField.clear();
            passwordField.clear();
            emailField.clear();
            phoneNumberField.clear();
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }

    }

    public void onSignIn() throws IOException {
        // check fields with  database

        DemoApp.setRoot("primary");
    }
}
