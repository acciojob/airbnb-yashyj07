package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelManagementRepository {

    Map<String, Hotel> hotel_db = new HashMap<>();
    Map<Integer, User> user_db = new HashMap<>();
    Map<String, Booking> booking_db = new HashMap<>();
    Map<Integer, List<Booking>> user_booking_db = new HashMap<>();

    private String hotelWithMaxFacility = "";
    private int maxFacilitiesCount = 0;
    public String addHotel(Hotel hotel) {

        if(hotel==null) return "FAILURE";
        String key = hotel.getHotelName();
        if(key == null){
            return "FAILURE";
        }
        if(hotel_db.containsKey(key)){
            return "FAILURE";
        }

        hotel_db.put(key, hotel);

        int countOfFacilitiesInHotel = hotel.getFacilities().size();

        if(countOfFacilitiesInHotel>=maxFacilitiesCount){
            if(countOfFacilitiesInHotel==maxFacilitiesCount){
                if(hotel.getHotelName().compareTo(hotelWithMaxFacility)<0){
                    hotelWithMaxFacility = hotel.getHotelName();
                }
            }else{
                maxFacilitiesCount = countOfFacilitiesInHotel;
                hotelWithMaxFacility = hotel.getHotelName();
            }
        }
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        user_db.put(user.getaadharCardNo(), user);
        user_booking_db.put(user.getaadharCardNo(), new ArrayList<>());
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        return hotelWithMaxFacility;
    }

    public int bookARoom(Booking booking) {

        UUID uuid = UUID.randomUUID();
        String bookingId = uuid.toString();
        booking.setBookingId(bookingId);

        String hotelName = booking.getHotelName();
        Hotel hotel = hotel_db.get(hotelName);
        int pricePerNight = hotel.getPricePerNight();
        int noOfRooms = booking.getNoOfRooms();
        int availableRooms = hotel.getAvailableRooms();

        if(noOfRooms > availableRooms){
            return -1;
        }
        int amountPaid = noOfRooms * pricePerNight;
        booking.setAmountToBePaid(amountPaid);

        hotel.setAvailableRooms(availableRooms - noOfRooms);
        booking_db.put(bookingId, booking);
        hotel_db.put(hotelName, hotel);

        return amountPaid;

//        Hotel hotelToBeBooked = hotel_db.get(booking.getHotelName());
//        if(booking.getNoOfRooms()>hotelToBeBooked.getAvailableRooms()){
//            return -1;
//        }else{
//            hotelToBeBooked.setAvailableRooms(hotelToBeBooked.getAvailableRooms()-booking.getNoOfRooms());
//            booking.setBookingId(String.valueOf(UUID.randomUUID()));
//            booking.setAmountToBePaid(booking.getNoOfRooms()*hotelToBeBooked.getPricePerNight());
//            booking_db.put(booking.getBookingId(), booking);
//            user_booking_db.get(booking.getBookingAadharCard()).add(booking);
//            return booking.getAmountToBePaid();
//        }
    }

    public int getBookings(Integer aadharCard) {
        return user_booking_db.get(aadharCard).size();
    }


    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        if (!hotel_db.containsKey(hotelName)){
            return new Hotel();
        }

        Hotel currHotel = hotel_db.get(hotelName);

        for(Facility facility: newFacilities){
            if(!currHotel.getFacilities().contains(facility)){
                currHotel.getFacilities().add(facility);
            }
        }


        int countOfFacilitiesInHotel = currHotel.getFacilities().size();

        if(countOfFacilitiesInHotel>=maxFacilitiesCount){
            if(countOfFacilitiesInHotel==maxFacilitiesCount){
                if(currHotel.getHotelName().compareTo(hotelWithMaxFacility)<0){
                    hotelWithMaxFacility = currHotel.getHotelName();
                }
            }else{
                maxFacilitiesCount = countOfFacilitiesInHotel;
                hotelWithMaxFacility = currHotel.getHotelName();
            }
        }
        return currHotel;
    }
}
