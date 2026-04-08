package service;

import dao.BookingDao;
import java.util.*;

public class BookingService implements IBookingService {

    private BookingDao bookingDao = new BookingDao();

    public boolean createBooking(int customerId, int modelId, int dealerId, boolean available) {

        String status = available ? "Available" : "Unavailable";
        return bookingDao.addBooking(customerId, modelId, dealerId, status);
    }

    public List<Object[]> getBookings(int dealerId) {
        return bookingDao.getBookings(dealerId);
    }

    public boolean removeBooking(int bookingId) {
        return bookingDao.deleteBooking(bookingId);
    }

    public void updateBookingStatus(int modelId, int dealerId) {
        bookingDao.updateBookingStatus(modelId, dealerId);
    }

    public boolean createBooking(int customerId, int modelId, int dealerId) {

        
        boolean isAvailable = bookingDao.checkCarAvailability(modelId, dealerId);

        return createBooking(customerId, modelId, dealerId, isAvailable);
    }
}