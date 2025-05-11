package com.klef.fsd.sdp.dto;

import java.time.LocalDateTime;

import com.klef.fsd.sdp.model.BookEvent;

public class BookEventDTO {
    private int id;
    private int eventId; // Or use the full EventDTO, depending on what you need
    private String startdate;
    private String enddate;
    private int bookedcapacity;
    private String status;
    private LocalDateTime bookingtime;

    // Constructors, getters, setters, etc.
    
    public BookEventDTO(BookEvent bookEvent) {
        this.id = bookEvent.getId();
        this.eventId = bookEvent.getEvent().getId(); // You can choose to include other details
        this.startdate = bookEvent.getStartdate();
        this.enddate = bookEvent.getEnddate();
        this.bookedcapacity = bookEvent.getBookedcapacity();
        this.status = bookEvent.getStatus();
        this.bookingtime = bookEvent.getBookingtime();
    }
}

