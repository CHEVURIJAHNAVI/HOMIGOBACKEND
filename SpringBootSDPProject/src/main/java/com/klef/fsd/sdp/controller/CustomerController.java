package com.klef.fsd.sdp.controller;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.dto.EventDTO;
import com.klef.fsd.sdp.model.BookEvent;
import com.klef.fsd.sdp.model.Customer;
import com.klef.fsd.sdp.model.Event;
import com.klef.fsd.sdp.service.CustomerService;

@RestController
@RequestMapping("/customer")
@CrossOrigin("*") // * means any URL
public class CustomerController 
{
   @Autowired
   private CustomerService customerService;
	
   @GetMapping("/")
   public String home()
   {
	   return "FSD SDP Project";
   }
   
   @PostMapping("/registration")
   public ResponseEntity<String> customerregistration(@RequestBody Customer customer)
   {
	   try
	   {
		  String output = customerService.customerregistration(customer);
		  return ResponseEntity.ok(output); // 200 - success
	   }
	   catch(Exception e)
	   {
		   //return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
		   return ResponseEntity.status(500).body("Customer Registration Failed ...");
	   }
   }
   
   @PostMapping("/checkcustomerlogin")
   public ResponseEntity<?> checkcustomerlogin(@RequestBody Customer customer) 
   {
       try 
       {
           Customer c = customerService.checkcustomerlogin(customer.getUsername(), customer.getPassword());

           if (c!=null) 
           {
               return ResponseEntity.ok(c); // if login is successful
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
   
   @PutMapping("/updateprofile")
   public ResponseEntity<String> customerupdateprofile(@RequestBody Customer customer)
   {
 	  try
 	   {
 		  System.out.println(customer.toString());
 		  String output = customerService.customerupdateprofile(customer);
 		  return ResponseEntity.ok(output);
 	   }
 	   catch(Exception e)
 	   {
 		    System.out.println(e.getMessage());
 		    return ResponseEntity.status(500).body("Failed to Update Customer ... !!"); 
 	   }
   }

   @GetMapping("/viewallevents")
   public ResponseEntity<List<EventDTO>> viewAllEvents() {
       List<Event> events = customerService.viewallevents();

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
               e.printStackTrace();
           }

           return dto;
       }).collect(Collectors.toList());

       return ResponseEntity.ok(eventDTOs);
   }


   
  
   @PostMapping("/bookevent")
   public ResponseEntity<String> bookEvent(@RequestBody BookEvent bookEvent) 
   {
      try
      {
    	  int bookingId = new Random().nextInt(900000) + 100000;  // 6-digit ID
          bookEvent.setId(bookingId);

          Customer customer = customerService.getCustomerById(bookEvent.getCustomer().getId());
          Event event = customerService.getEventById(bookEvent.getEvent().getId());
         

          // Assign actual customer and event objects
          bookEvent.setCustomer(customer);
          bookEvent.setEvent(event);

          // Set status to "BOOKED"
          bookEvent.setStatus("BOOKED");

          String output = customerService.bookevent(bookEvent);

          return ResponseEntity.ok(output); // 200 - success
      }
      catch (Exception e) 
      {
    	  return ResponseEntity.status(500).body("Failed to Book an Event: " + e.getMessage());
	  }
   }

   @GetMapping("/bookedevents/{cid}")
   public ResponseEntity<List<BookEvent>> getEventsByCustomer(@PathVariable int cid) 
   {
       List<BookEvent> bookedevents =  customerService.getbookedeventsByCustomer(cid);
   	 
   	   return ResponseEntity.ok(bookedevents); // 200 - success
   }  
   
   
}