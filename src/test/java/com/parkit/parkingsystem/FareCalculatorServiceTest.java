package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private Date inTime = new Date();
	private Date outTime = new Date();
	private TicketDAO ticketDao = new TicketDAO();

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();

	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
		ticketDao.dataBaseConfig = new DataBaseTestConfig();
	}

	@Test
	public void calculateFareCar() {

		// we try to see if price of ticket is equal to the Care Rate Per Hour for one
		// hour
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals(ticket.getPrice(), (Fare.CAR_RATE_PER_HOUR));

	}

	@Test
	public void calculateFareBike() {
		// same as the previous test but for Bike
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareUnkownType() {
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		// we want to be sure if there is no vehicule type the FareCalculator class will
		// throwing an exception
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));
	}

	@Test
	public void calculateFareBikeWithFutureInTime() {
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		// Same as previously but for IllegalArgumentException like an out time before
		// in time
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		// 45 minutes parking time should give 3/4th parking fare
		// we want to see if little amount of time make bug
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		// like as before but for Car
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {
		// 25 hours parking time should give 25 parking fare per hour
		inTime.setTime(System.currentTimeMillis() - (25 * 60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals((25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarTypeWithLessThanThirtyMinutesParkingTime() {
		// we test if it's of for the 30min free for car (exactly 29min)
		inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals((0), ticket.getPrice());

	}

	@Test
	public void calculateFareBikeTypeWithLessThanThirtyMinutesParkingTime() {
		// 30min free for bike and
		// with another duration
		inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals(0, ticket.getPrice());

	}

	@Test
	public void calculateFareCarWithRecurringFivePercentDiscount() {
		// We want to know if the discount of 5% is ok for one hour it should be equal
		// to CAR_WITH_DISCOUNT fare

		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setParkingSpot(parkingSpot);

		ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket2 = new Ticket();
		ticket2.setInTime(inTime);
		ticket2.setOutTime(outTime);
		ticket2.setVehicleRegNumber("ABCDEF");
		ticket2.setParkingSpot(parkingSpot2);
		ticketDao.saveTicket(ticket2);

		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals(Fare.CAR_WITH_DISCOUNT, ticket.getPrice());

	}

	@Test
	public void calculateFareBikeWithRecurringFivePercentDiscount() {
		// We want to know if the discount of 5% is ok for one hour it should be equal
		// to BIKE_WITH_DISCOUNT fare

		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setParkingSpot(parkingSpot);

		ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.BIKE, false);
		Ticket ticket2 = new Ticket();
		ticket2.setInTime(inTime);
		ticket2.setOutTime(outTime);
		ticket2.setVehicleRegNumber("ABCDEF");
		ticket2.setParkingSpot(parkingSpot2);
		ticketDao.saveTicket(ticket2);

		fareCalculatorService.calculateFare(ticket, ticketDao);
		assertEquals(Fare.BIKE_WITH_DISCOUNT, ticket.getPrice());
	}
}
