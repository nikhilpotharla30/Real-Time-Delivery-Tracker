package com.example.firebase_backend;
import com.google.firebase.database.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class LocationController {
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

   
    @PostMapping("/location/{driverId}")
    public String updateLocation(@PathVariable String driverId, @RequestBody Map<String, Object> data) {
        driverId = driverId.trim(); // ✅ Remove newline/space characters
        dbRef.child("drivers").child(driverId).setValueAsync(data);
        return "✅ Location updated for: " + driverId;
    }

   
    @GetMapping("/location/{driverId}")
    public CompletableFuture<Map<String, Object>> getLocation(@PathVariable String driverId) {
        driverId = driverId.trim(); // ✅ Remove newline/space characters
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        dbRef.child("drivers").child(driverId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> location = (Map<String, Object>) snapshot.getValue();
                    future.complete(location);
                }

                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException("Firebase error: " + error.getMessage()));
                }
            });

        return future;
    }
}
