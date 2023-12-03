package kz.kbtu.sxodimkbtu;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


@Service
public class FBInitialize {

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-credentials.json");

            if (serviceAccount == null) {
                throw new FileNotFoundException("File firebase-service-credentials.json not found on the classpath");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://sxodimkbtu-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
