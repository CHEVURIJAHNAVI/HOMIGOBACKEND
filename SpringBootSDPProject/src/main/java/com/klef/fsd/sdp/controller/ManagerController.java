package com.klef.fsd.sdp.controller;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.klef.fsd.sdp.dto.EventDTO;
import com.klef.fsd.sdp.model.BookEvent;
import com.klef.fsd.sdp.model.Event;
import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.service.ManagerService;
@CrossOrigin("*")
@RestController
@RequestMapping("/manager")
public class ManagerController 
{
   @Autowired
   private ManagerService managerService;
	   
   @PostMapping("/checkmanagerlogin")
   public ResponseEntity<?> checkmanagerlogin(@RequestBody Manager manager) 
   {
       try 
       {
           Manager m = managerService.checkmanagerlogin(manager.getUsername(), manager.getPassword());

           if (m!=null) 
           {
               return ResponseEntity.ok(m); // if login is successful
           } 
           else 
           {
               return ResponseEntity.status(401).body("Invalid Username or Password"); // if login is fail
           }
       } 
       catch (Exception e) 
       {
           return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
       }
   }

   @PostMapping("/addevent")
   public ResponseEntity<String> addevent(
           @RequestParam String category,
           @RequestParam String title,
           @RequestParam String description,
           @RequestParam int capacity,
           @RequestParam double cost,
           @RequestParam int manager_id,
           @RequestParam("eventimage") MultipartFile file) {
       try {
           Manager manager = managerService.getManagerById(manager_id);

           byte[] bytes = file.getBytes();
           Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

           Event event = new Event();
           event.setCategory(category);
           event.setTitle(title);
           event.setDescription(description);
           event.setCapacity(capacity);
           event.setCost(cost);
           event.setImage(blob); // Assuming Event entity has an image field of type Blob
           event.setManager(manager);

           String output = managerService.addevent(event);
           return ResponseEntity.ok(output); // 200 - success
       } catch (Exception e) {
           return ResponseEntity.status(500).body("Failed to Add Service: " + e.getMessage());
       }
   }

   
   @GetMapping("/vieweventsbymanager/{id}")
   public ResponseEntity<List<EventDTO>> vieweventsbymanager(@PathVariable int id) {
       List<Event> events = managerService.vieweventsbymanager(id);

       List<EventDTO> eventDTOs = events.stream().map(event -> {
           EventDTO dto = new EventDTO();
           dto.setId(event.getId());
           dto.setTitle(event.getTitle());
           dto.setDescription(event.getDescription());
           dto.setCategory(event.getCategory());
           dto.setCost(event.getCost());
           dto.setCapacity(event.getCapacity());

           try {
               Blob imageBlob = event.getImage();
               if (imageBlob != null) {
                   byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                   String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                   dto.setBase64Image(base64Image);
               }
           } catch (Exception e) {
               e.printStackTrace(); // You might want to log properly
           }

           return dto;
       }).collect(Collectors.toList());

       return ResponseEntity.ok(eventDTOs);
   }



   @GetMapping("/viewbookingsbymanager/{managerId}")
   public ResponseEntity<List<BookEvent>> viewBookingsByManager(@PathVariable int managerId) 
   { 
       List<BookEvent> events = managerService.getbookingsbyManager(managerId);
       return ResponseEntity.ok(events);
   }
   @DeleteMapping("/delete/{id}")
   public ResponseEntity<String> deleteEvent(@PathVariable int id) {
       try {
           String result = managerService.deleteEvent(id);
           return ResponseEntity.ok(result);
       } catch (Exception e) {
           return ResponseEntity.status(500).body("Error deleting event: " + e.getMessage());
       }
   }
   @GetMapping("/updatebookingstatus")
   public ResponseEntity<String> updateBookingStatus(@RequestParam int id,@RequestParam String status) 
   { 
       try
       {
    	   String output = managerService.updatebookingstatus(id, status);
    	   return ResponseEntity.ok(output);
       }
       catch (Exception e) 
       {
    	   System.out.println(e.getMessage());
    	   return ResponseEntity.status(500).body("Error:" + e.getMessage());
	   }
   }


   
}