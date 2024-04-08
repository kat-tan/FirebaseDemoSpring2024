package aydin.firebasedemospring2024;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.internal.NonNull;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static aydin.firebasedemospring2024.DemoApp.fstore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WelcomeController {
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

    public static FirebaseAuth fauth;

    public boolean onRegisterBtn() {
        if (!passwordField.getText().isEmpty() &&
                !emailField.getText().isEmpty() && !phoneNumberField.getText().isEmpty()) {
            String email = emailField.getText();
            String password = passwordField.getText();
            System.out.println(password);

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(true)
                    .setPassword(password)
                    .setPhoneNumber(phoneNumberField.getText())
                    .setDisabled(false);

            UserRecord userRecord;
            try {
                userRecord = DemoApp.fauth.createUser(request);
                System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid());

                DocumentReference docRef = fstore.collection("App Users").document(UUID.randomUUID().toString());

                Map<String, Object> data = new HashMap<>();
                data.put("Email", email);
                data.put("Password", password);

                ApiFuture<WriteResult> result = docRef.set(data);

                passwordField.clear();
                emailField.clear();
                phoneNumberField.clear();
                return true;

            } catch (FirebaseAuthException ex) {
                // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error creating a new user in the firebase.");
                return false;
            }
        }

        return false;

    }

    public void onSignIn() throws IOException, FirebaseAuthException {
        fauth = FirebaseAuth.getInstance();

        String userEmail = signInEmailField.getText();
        String userPassword = signInPasswordField.getText();

        try {
            UserRecord record = fauth.getUserByEmail(userEmail);

            if (record.isEmailVerified()) {
                ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("App Users").get();

                List<QueryDocumentSnapshot> documents;
                try {
                    documents = future.get().getDocuments();
                    if(!documents.isEmpty()) {
                        boolean found = false;
                        for (QueryDocumentSnapshot document : documents) {
                            if (document.getString("Password").equals(userPassword)) {
                                DemoApp.setRoot("primary");
                                found = true;
                                System.out.println("Sign In Successful.");
                            }
                        }
                        if (found == false) {
                            System.out.println("Sign In Failed.");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Error");
                            alert.setHeaderText("Invalid Input");
                            alert.setContentText("Password is incorrect.");

                            alert.showAndWait();
                        }
                    }

            } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FirebaseAuthException exception) {
            System.out.println("Sign In Failed.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Email is not verified or it is not registered.");

            alert.showAndWait();
        }


    }
}
