package kz.kbtu.sxodimkbtu.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import kz.kbtu.sxodimkbtu.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class EventService {
    public static final String COL_NAME="events";
    //get Events
    public List<Event> getEvents() throws InterruptedException, ExecutionException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = dbFireStore.collection(COL_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Event> events = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            events.add(document.toObject(Event.class));
        }
        return events;
    }

    //Create a doc in FireStore
    public String saveEventDetails(Event event) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve the last event ID
        Query lastEventQuery = dbFirestore.collection(COL_NAME).orderBy("id", Query.Direction.DESCENDING).limit(1);
        ApiFuture<QuerySnapshot> lastEventFuture = lastEventQuery.get();
        QuerySnapshot lastEventSnapshot = lastEventFuture.get();

        Long lastEventId = null;
        if (!lastEventSnapshot.isEmpty()) {
            DocumentSnapshot lastEventDocument = lastEventSnapshot.getDocuments().get(0);
            lastEventId = lastEventDocument.getLong("id");
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
    public Event getEventDetails(int eventID) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(String.valueOf(eventID));
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Event event = null;
        if(document.exists()) {
            event = document.toObject(Event.class);
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
}
