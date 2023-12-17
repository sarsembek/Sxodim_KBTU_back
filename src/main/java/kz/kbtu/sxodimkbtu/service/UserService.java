package kz.kbtu.sxodimkbtu.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import kz.kbtu.sxodimkbtu.model.UserRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UserService {
    private static final String USERS_COLLECTION = "users";

    public UserRegistration getUserRegistrations(String userID) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference userDocRef = dbFirestore.collection(USERS_COLLECTION).document(userID);

        DocumentSnapshot userSnapshot = userDocRef.get().get();

        UserRegistration userRegistration = null;

        if(userSnapshot.exists()) {
            String userUid = userSnapshot.getId();
            List<Long> eventIDs = userSnapshot.contains("eventIDs")
                    ? (List<Long>) userSnapshot.get("eventIDs")
                    : new ArrayList<>();
            userRegistration = new UserRegistration(userUid, eventIDs);
        }
        return userRegistration;
    }

    public String getUserIdByFirebaseId(String firebaseId) throws FirebaseAuthException {
        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Verify the ID token
        FirebaseToken decodedToken = auth.verifyIdToken(firebaseId);

        // Get the user ID from the decoded token
        return decodedToken.getUid();
    }
    public static UserRecord getUserInfoByUid(String uid) throws FirebaseAuthException {
        // Initialize Firebase Admin SDK
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Get user record by UID
        UserRecord userRecord = auth.getUser(uid);

        return userRecord;
    }

}
