//package com.driver.controllers;
//
//import com.driver.model.Booking;
//import com.driver.model.Facility;
//import com.driver.model.Hotel;
//import com.driver.model.User;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class HotelManagementRepository {
//
//    Map<String, Hotel> hotel_db = new HashMap<>();
//    Map<Integer, User> user_db = new HashMap<>();
//    Map<String, Booking> booking_db = new HashMap<>();
//    Map<Integer, List<Booking>> user_booking_db = new HashMap<>();
//
//    private String hotelWithMaxFacility = "";
//    private int maxFacilitiesCount = 0;
//    public String addHotel(Hotel hotel) {
//
//        if(hotel==null) return "FAILURE";
//        String key = hotel.getHotelName();
//        if(key == null){
//            return "FAILURE";
//        }
//        if(hotel_db.containsKey(key)){
//            return "FAILURE";
//        }
//
//        hotel_db.put(key, hotel);
//
//        int countOfFacilitiesInHotel = hotel.getFacilities().size();
//
//        if(countOfFacilitiesInHotel>=maxFacilitiesCount){
//            if(countOfFacilitiesInHotel==maxFacilitiesCount){
//                if(hotel.getHotelName().compareTo(hotelWithMaxFacility)<0){
//                    hotelWithMaxFacility = hotel.getHotelName();
//                }
//            }else{
//                maxFacilitiesCount = countOfFacilitiesInHotel;
//                hotelWithMaxFacility = hotel.getHotelName();
//            }
//        }
//        return "SUCCESS";
//    }
//
//    public Integer addUser(User user) {
//        user_db.put(user.getaadharCardNo(), user);
//        user_booking_db.put(user.getaadharCardNo(), new ArrayList<>());
//        return user.getaadharCardNo();
//    }
//
//    public String getHotelWithMostFacilities() {
//        return hotelWithMaxFacility;
//    }
//
//    public int bookARoom(Booking booking) {
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
//    }
//
//    public int getBookings(Integer aadharCard) {
//        return user_booking_db.get(aadharCard).size();
//    }
//
//
//    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
//
//        if (!hotel_db.containsKey(hotelName)){
//            return new Hotel();
//        }
//
//        Hotel currHotel = hotel_db.get(hotelName);
//
//        for(Facility facility: newFacilities){
//            if(!currHotel.getFacilities().contains(facility)){
//                currHotel.getFacilities().add(facility);
//            }
//        }
//
//
//        int countOfFacilitiesInHotel = currHotel.getFacilities().size();
//
//        if(countOfFacilitiesInHotel>=maxFacilitiesCount){
//            if(countOfFacilitiesInHotel==maxFacilitiesCount){
//                if(currHotel.getHotelName().compareTo(hotelWithMaxFacility)<0){
//                    hotelWithMaxFacility = currHotel.getHotelName();
//                }
//            }else{
//                maxFacilitiesCount = countOfFacilitiesInHotel;
//                hotelWithMaxFacility = currHotel.getHotelName();
//            }
//        }
//        return currHotel;
//    }
//}

package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class HotelManagementRepository {

    HashMap<String, Hotel> hotelDb = new HashMap<>();
    HashMap<Integer, User> userDb = new HashMap<>();
    HashMap<String, Booking> bookingDb = new HashMap<>();
//    HashMap<String, >

    public String addHotel(Hotel hotel){
        String key = hotel.getHotelName();

        if(key == null){
            return "FAILURE";
        }
        else if(hotelDb.containsKey(key)){
            return "FAILURE";
        }
        else{
            hotelDb.put(key, hotel);
        }
        return "SUCCESS";
    }

    public Integer addUser(@RequestBody User user){
        int key = user.getaadharCardNo();

        userDb.put(key, user);

        return key;
    }

    public Integer bookARoom(Booking booking){
        UUID uuid = UUID.randomUUID();
        String bookingId = uuid.toString();
        booking.setBookingId(bookingId);

        String hotelName = booking.getHotelName();
        Hotel hotel = hotelDb.get(hotelName);
        int pricePerNight = hotel.getPricePerNight();
        int noOfRooms = booking.getNoOfRooms();
        int availableRooms = hotel.getAvailableRooms();

        if(noOfRooms > availableRooms){
            return -1;
        }
        int amountPaid = noOfRooms * pricePerNight;
        booking.setAmountToBePaid(amountPaid);

        hotel.setAvailableRooms(availableRooms - noOfRooms);
        bookingDb.put(bookingId, booking);
        hotelDb.put(hotelName, hotel);

        return amountPaid;
    }

    public int getBookings(Integer aadharCard){
        int ans = 0;

        for(String bookingId : bookingDb.keySet()){
            Booking booking = bookingDb.get(bookingId);
            if(booking.getBookingAadharCard() == aadharCard){
                ans++;
            }
        }
        return ans;
    }

    public String getHotelWithMostFacilities(){
        int noOfFacilities = 0;
        String ans = "";

        for(String hotelName : hotelDb.keySet()){
            Hotel hotel = hotelDb.get(hotelName);
            if(hotel.getFacilities().size() > noOfFacilities){
                ans = hotelName;
                noOfFacilities = hotel.getFacilities().size();
            }
            else if(hotel.getFacilities().size() == noOfFacilities){
                if(hotelName.compareTo(ans) < 0){
                    ans = hotelName;
                }
            }
        }
        return ans;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName){
        Hotel hotel = hotelDb.get(hotelName);
        List<Facility> currentFacilities = hotel.getFacilities();

        for(Facility facility : newFacilities){
            if(!currentFacilities.contains(facility)){
                currentFacilities.add(facility);
            }
        }
        hotel.setFacilities(currentFacilities);
        hotelDb.put(hotelName, hotel);

        return hotel;
    }
}
