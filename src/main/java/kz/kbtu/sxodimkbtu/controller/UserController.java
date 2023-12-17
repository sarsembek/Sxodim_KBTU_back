package kz.kbtu.sxodimkbtu.controller;

import com.google.firebase.auth.FirebaseAuthException;
import kz.kbtu.sxodimkbtu.model.UserRegistration;
import kz.kbtu.sxodimkbtu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserId")
    public ResponseEntity<String> getUserId(@RequestParam String firebaseId) {
        try {
            String userId = userService.getUserIdByFirebaseId(firebaseId);
            return new ResponseEntity<>(userId, HttpStatus.OK);
        } catch (FirebaseAuthException e) {
            // Handle authentication exception
            return new ResponseEntity<>("Error getting user ID", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getUserRegistrations")
    public UserRegistration getUserRegistration(@RequestParam String userID) {
        try {
            return userService.getUserRegistrations(userID);
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return null; // Or return an error response
        }
    }
}
