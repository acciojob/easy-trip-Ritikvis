package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;

import java.util.Date;

public class AirportService {

    AirportRepository airportRepositoryObj = new AirportRepository();
    public void addairport(Airport airport){
        airportRepositoryObj.addairport(airport);
    }

    public String getLargestAirportName(){
        return airportRepositoryObj.getLargestAirportName();
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromcity, City tocity)
    {
        return airportRepositoryObj.getShortestDurationOfPossibleBetweenTwoCities(fromcity,tocity);
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        return airportRepositoryObj.getNumberOfPeopleOn(date,airportName);
    }
    public int calculateFlightFare(Integer flightId){
        return airportRepositoryObj.calculateFlightFare(flightId);
    }
    public String bookATicket(Integer flightId, Integer passengerId){
        return airportRepositoryObj.bookATicket(flightId,passengerId);
    }
    public String cancelATicket(Integer flightId, Integer passengerId){
        return airportRepositoryObj.cancelATicket(flightId,passengerId);
    }
    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        return airportRepositoryObj.countOfBookingsDoneByPassengerAllCombined(passengerId);
    }
    public void addFlight(Flight flight){
        airportRepositoryObj.addFlight(flight);
    }

    public String getAirportNameFromFlightId(Integer flightId){
        return airportRepositoryObj.getAirportNameFromFlightId(flightId);
    }
    public int calculateRevenueOfAFlight(Integer flightId){
        return airportRepositoryObj.calculateRevenueOfAFlight(flightId);
    }

    public void addPassenger(Passenger passenger){
        airportRepositoryObj.addPassenger(passenger);
    }
}