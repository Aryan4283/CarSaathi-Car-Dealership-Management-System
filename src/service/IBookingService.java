package service;

import java.util.List;

public interface IBookingService {

    boolean createBooking(int customerId, int modelId, int dealerId, boolean available);

    List<Object[]> getBookings(int dealerId);

    boolean removeBooking(int bookingId);

    void updateBookingStatus(int modelId, int dealerId);
}