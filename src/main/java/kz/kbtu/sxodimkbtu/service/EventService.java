package kz.kbtu.sxodimkbtu.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.persistence.criteria.CriteriaBuilder;
import kz.kbtu.sxodimkbtu.model.Department;
import kz.kbtu.sxodimkbtu.model.Event;
import kz.kbtu.sxodimkbtu.model.Registration;
import kz.kbtu.sxodimkbtu.model.UserRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;


@Slf4j
@Service
public class EventService {
    public static final String COL_NAME="events";
    private static final String USERS_COLLECTION = "users";

    //get Events
    public List<Event> getEvents() throws InterruptedException, ExecutionException, FirebaseAuthException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = dbFireStore.collection(COL_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Event> events = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Event event = document.toObject(Event.class);

            String departmentId = event.getDepartmentId();
            String organizerId = event.getOrganizerId();
            UserRecord organizer = UserService.getUserInfoByUid(organizerId);
            Department department = DepartmentService.getDepartmentDetails(Integer.parseInt(departmentId));
            event.setOrganizerId(organizer.getEmail());
            assert department != null;
            event.setDepartmentId(department.getDepartmentName());

            events.add(event);
        }
        return events;
    }

    //Create a doc in FireStore
    public String saveEventDetails(Event event) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve the last event ID
        Query lastEventQuery = dbFirestore.collection(COL_NAME).orderBy("eventID", Query.Direction.DESCENDING).limit(1);
        ApiFuture<QuerySnapshot> lastEventFuture = lastEventQuery.get();
        QuerySnapshot lastEventSnapshot = lastEventFuture.get();

        Long lastEventId = null;
        if (!lastEventSnapshot.isEmpty()) {
            DocumentSnapshot lastEventDocument = lastEventSnapshot.getDocuments().get(0);
            lastEventId = lastEventDocument.getLong("eventID");
            log.info(String.valueOf(lastEventId));
        }

        // Generate the new event ID
        Long newEventId = lastEventId == null ? 1 : lastEventId + 1;

        // Update the event object with the new ID
        event.setEventID(newEventId);

        // Add the event to Firestore
        ApiFuture<WriteResult> collectionsApiFuture =
                dbFirestore.collection(COL_NAME).document(event.getName()).set(event);

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    //Get event from FireStore
    public Event getEventDetails(int eventID) throws InterruptedException, ExecutionException, FirebaseAuthException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference events = dbFirestore.collection(COL_NAME);
        QuerySnapshot querySnapshot = events.whereEqualTo("eventID",eventID).get().get();

        QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
        Event event = null;
        if(document.exists()) {
            event = document.toObject(Event.class);
            String departmentId = event.getDepartmentId();
            String organizerId = event.getOrganizerId();
            Department department = DepartmentService.getDepartmentDetails(Integer.parseInt(departmentId));
            UserRecord organizer = UserService.getUserInfoByUid(organizerId);

            event.setOrganizerId(organizer.getEmail());
            assert department != null;
            event.setDepartmentId(department.getDepartmentName());
            return event;
        }else {
            return null;
        }
    }

    //Update event in FireStore
    public String updateEventDetails(Event event) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture =
                dbFirestore.collection(COL_NAME).document(String.valueOf(event.getEventID())).set(event);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    //Delete event from FireStore
    public String deleteEvent(int eventID) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(String.valueOf(eventID)).delete();
        return "Document with Event ID "+eventID+" has been deleted";
    }

    public String registerUserToEvent(String userID, int eventID) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference eventsCollection = dbFirestore.collection(COL_NAME);
        ApiFuture<QuerySnapshot> querySnapshot = eventsCollection.whereEqualTo("eventID", eventID).get();

        QuerySnapshot documents = querySnapshot.get();

        List<QueryDocumentSnapshot> documentSnapshots = documents.getDocuments();

        Registration registration = new Registration(userID,new Date());
        Map<String, Object> updates = new HashMap<>();
        updates.put("registrations", FieldValue.arrayUnion(registration));

        for (QueryDocumentSnapshot document: documentSnapshots) {
            Event event = document.toObject(Event.class);
            DocumentReference documentRef = eventsCollection.document(event.getName());
            ApiFuture<WriteResult> updateFuture = documentRef.update(updates);
        }

        DocumentReference userDocRef = dbFirestore.collection(USERS_COLLECTION).document(userID);
        DocumentSnapshot userSnapshot = userDocRef.get().get();
        log.info(userSnapshot.toString());
        if (userSnapshot.exists()) {
            List<Integer> eventIDs = userSnapshot.contains("eventIDs")
                    ? (List<Integer>) userSnapshot.get("eventIDs")
                    : new ArrayList<>(); // Assuming "eventIDs" is the field name

            // Add the eventId to the array
            eventIDs.add(eventID);
            userDocRef.update("userID", userID);
            // Update the "eventIDs" field
            userDocRef.update("eventIDs", eventIDs);
        } else {
            // Document doesn't exist, create a new one
            Map<String, Object> newUser = Collections.singletonMap("eventIDs", Arrays.asList(eventID));
            userDocRef.set(newUser);
        }

        return "User with ID: " + userID + " was registered to event with ID: " + eventID;
    }

    public String deleteEventFromRegistrations(String userID, int eventID) throws ExecutionException, InterruptedException{
        deleteEventFromUser(userID, eventID);
        deleteUserFromEvent(userID, eventID);
        return "Event with ID: " + eventID + " removed";
    }
    public void deleteEventFromUser(String userID, int eventID) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(USERS_COLLECTION).document(userID);
        // Get the DocumentSnapshot asynchronously
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        // Wait for the result
        DocumentSnapshot documentSnapshot = future.get();

        if (documentSnapshot.exists()) {
            UserRegistration userRegistration = documentSnapshot.toObject(UserRegistration.class);
//            log.info(userRegistration.toString());
            if (userRegistration != null) {
                List<Long> currentArray = userRegistration.getEventIDs();
                Long eventIDToRemove = (long) eventID;
                currentArray.removeIf(target -> (target == eventIDToRemove));
                log.info(currentArray.toString());
                documentReference.update("eventIDs", currentArray);
            }
        }
    }
    public void deleteUserFromEvent(String userID, int eventID) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference eventsCollection = dbFirestore.collection(COL_NAME);
        ApiFuture<QuerySnapshot> querySnapshot = eventsCollection
                .whereEqualTo("eventID", eventID)
                .get();
        QuerySnapshot documents = querySnapshot.get();
        for (QueryDocumentSnapshot document : documents) {
            List<Map<String, Object>> registrations = (List<Map<String, Object>>) document.get("registrations");
            if (registrations != null) {
                registrations.removeIf(registration -> userID.equals(registration.get("userID")));
                // Update the document with the modified registrations array
                DocumentReference documentRef = eventsCollection.document(document.getId());
                documentRef.update("registrations", registrations);
            }
        }
    }

}
