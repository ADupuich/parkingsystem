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

		double roundedDurationInPartOfHour = (double) Math.round(((outTime - inTime) / (1000.0 * 60.0 * 60.0)) * 1000)
				/ 1000;

		if (roundedDurationInPartOfHour < 0.5) {
			roundedDurationInPartOfHour = 0;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(roundedDurationInPartOfHour * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(roundedDurationInPartOfHour * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}