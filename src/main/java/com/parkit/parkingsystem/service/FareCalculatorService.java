package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, TicketDAO ticketDao) {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double inTime = ticket.getInTime().getTime();
		double outTime = ticket.getOutTime().getTime();

		double roundedDurationInPartOfHour = (double) Math.round(((outTime - inTime) / (1000.0 * 60.0 * 60.0)) * 1000)
				/ 1000;
		System.out.println(roundedDurationInPartOfHour);

		if (roundedDurationInPartOfHour < 0.5) {
			roundedDurationInPartOfHour = 0;
		}

		// TicketDAO ticketDao = ticketDao;
		// ticketDao.dataBaseConfig = new DataBaseConfig();

		switch (ticket.getParkingSpot().getParkingType()) {

		case CAR: {

			int ticketQuantity = ticketDao.countTicketByVehiculeRegNumber(ticket.getVehicleRegNumber());
			if (ticketQuantity > 1) {
				ticket.setPrice(roundedDurationInPartOfHour * Fare.CAR_WITH_DISCOUNT);
			} else {
				ticket.setPrice(roundedDurationInPartOfHour * Fare.CAR_RATE_PER_HOUR);
			}
			break;
		}
		case BIKE: {
			int ticketQuantity = ticketDao.countTicketByVehiculeRegNumber(ticket.getVehicleRegNumber());
			if (ticketQuantity > 1) {
				ticket.setPrice(roundedDurationInPartOfHour * Fare.BIKE_WITH_DISCOUNT);
			} else {
				ticket.setPrice(roundedDurationInPartOfHour * Fare.BIKE_RATE_PER_HOUR);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}
