package edu.university.equipment;
import static org.junit.jupiter.api.Assertions.*; import java.util.NoSuchElementException; import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test;
class BookingServiceTest {
 private BookingService service; @BeforeEach void setup(){service=new BookingService();}
 @Test void successfulBookingReducesInventory(){service.create("STU1001","LAPTOP01",2);assertEquals(8,service.getEquipment("LAPTOP01").getAvailableQuantity());}
 @Test void zeroQuantityRejected(){assertThrows(IllegalArgumentException.class,()->service.create("STU1001","LAPTOP01",0));assertEquals(10,service.getEquipment("LAPTOP01").getAvailableQuantity());}
 @Test void blankBorrowerRejected(){assertThrows(IllegalArgumentException.class,()->service.create(" ","LAPTOP01",1));}
 @Test void unknownEquipmentRejected(){assertThrows(NoSuchElementException.class,()->service.create("STU1001","UNKNOWN",1));}
 @Test void ordinaryOverbookingRejected(){assertThrows(IllegalStateException.class,()->service.create("STU1001","PROJECTOR01",6));assertEquals(4,service.getEquipment("PROJECTOR01").getAvailableQuantity());}
 @Test void cancellationRestoresInventoryOnce(){Booking b=service.create("STU1001","TABLET01",3);service.cancel(b.getId());assertEquals(8,service.getEquipment("TABLET01").getAvailableQuantity());assertThrows(IllegalStateException.class,()->service.cancel(b.getId()));}
@Test
void bookingExactlyAvailableInventoryIsAccepted() {
    Booking booking = service.create("STU1001", "PROJECTOR01", 4);

    assertEquals(4, booking.getQuantity());
    assertEquals(0, service.getEquipment("PROJECTOR01").getAvailableQuantity());
    assertEquals("CONFIRMED", booking.getStatus());
}

@Test
void exceedingAvailableInventoryDoesNotChangeInventoryOrCreateBooking() {
    int initialInventory =
        service.getEquipment("PROJECTOR01").getAvailableQuantity();
    int initialBookingCount = service.getBookingCount();

    assertThrows(
        IllegalStateException.class,
        () -> service.create("STU1001", "PROJECTOR01", 5)
    );

    assertEquals(
        initialInventory,
        service.getEquipment("PROJECTOR01").getAvailableQuantity()
    );

    assertEquals(initialBookingCount, service.getBookingCount());
}
@Test
void sequentialBookingsWithinInventoryAreAccepted() {
    Booking firstBooking = service.create("STU1001", "PROJECTOR01", 2);
    Booking secondBooking = service.create("STU1002", "PROJECTOR01", 2);

    assertEquals("CONFIRMED", firstBooking.getStatus());
    assertEquals("CONFIRMED", secondBooking.getStatus());

    assertEquals(2, firstBooking.getQuantity());
    assertEquals(2, secondBooking.getQuantity());

    assertEquals(
        0,
        service.getEquipment("PROJECTOR01").getAvailableQuantity()
    );
}
@Test
void sequentialBookingsCannotExceedTotalInventory() {
    Booking firstBooking = service.create("STU1001", "PROJECTOR01", 3);
    int initialBookingCount = service.getBookingCount();

    assertEquals("CONFIRMED", firstBooking.getStatus());

    assertThrows(
        IllegalStateException.class,
        () -> service.create("STU1002", "PROJECTOR01", 2)
    );

    assertEquals(
        1,
        service.getEquipment("PROJECTOR01").getAvailableQuantity()
    );

    assertEquals(initialBookingCount, service.getBookingCount());
}
}