package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double inTime = ticket.getInTime().getTime();
		double outTime = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		// nous obtenons le résultat en minute
		double durationInPartOfHour = (outTime - inTime) / 1000.00 / 60.00 / 60.00;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			// Nous voulons le nombre d'heurs pas de minutes de stationnement
			ticket.setPrice(durationInPartOfHour * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(durationInPartOfHour * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}