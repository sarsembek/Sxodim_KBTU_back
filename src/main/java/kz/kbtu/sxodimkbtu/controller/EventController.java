package kz.kbtu.sxodimkbtu.controller;

import kz.kbtu.sxodimkbtu.model.Event;
import kz.kbtu.sxodimkbtu.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping("/getEventDetails")
    public Event getEvent(@RequestParam String name ) throws InterruptedException, ExecutionException{
        return eventService.getEventDetails(name);
    }

    @PostMapping("/createEvent")
    public String createEvent(@RequestBody Event event ) throws InterruptedException, ExecutionException {
        return eventService.saveEventDetails(event);
    }

    @PutMapping("/updateEvent")
    public String updateEvent(@RequestBody Event event  ) throws InterruptedException, ExecutionException {
        return eventService.updateEventDetails(event);
    }

    @DeleteMapping("/deleteEvent")
    public String deleteEvent(@RequestParam String name) {
        return eventService.deleteEvent(name);
    }
}