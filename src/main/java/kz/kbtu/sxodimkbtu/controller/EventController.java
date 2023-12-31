package kz.kbtu.sxodimkbtu.controller;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import kz.kbtu.sxodimkbtu.model.Event;
import kz.kbtu.sxodimkbtu.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping("/getEvents")
    public List<Event> getEvents() throws InterruptedException, ExecutionException, FirebaseAuthException {
        return eventService.getEvents();
    }
    @GetMapping("/getEventDetails")
    public Event getEvent(@RequestParam int eventID) throws InterruptedException, ExecutionException, FirebaseAuthException {
        return eventService.getEventDetails(eventID);
    }

    @PostMapping("/createEvent")
    public String createEvent(@RequestBody Event event ) throws InterruptedException, ExecutionException {
        return eventService.saveEventDetails(event);
    }

    @PutMapping("/updateEvent")
    public String updateEvent(@RequestBody Event event ) throws InterruptedException, ExecutionException {
        return eventService.updateEventDetails(event);
    }

    @DeleteMapping("/deleteEvent")
    public String deleteEvent(@RequestParam int eventID) {
        return eventService.deleteEvent(eventID);
    }

    @PostMapping("/registerUserToEvent")
    public String registerUserToEvent(@RequestParam String userID, @RequestParam int eventID) throws InterruptedException, ExecutionException {
        return eventService.registerUserToEvent(userID, eventID);
    }

    @DeleteMapping("/deleteEventFromRegistrations")
    public String deleteEventFromRegistrations(@RequestParam String userID, @RequestParam int eventID) throws InterruptedException, ExecutionException {
        return eventService.deleteEventFromRegistrations(userID,eventID);
    }
}