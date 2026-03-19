package fulfillment;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FulfillmentActivitiesImpl implements FulfillmentActivities {

    private static final Logger log = LoggerFactory.getLogger(FulfillmentActivitiesImpl.class);

    @Override
    public String reserveInventory(Order order) {
        // TODO: Move the reserveInventory logic from FulfillmentPipeline here.
        //       Replace raw Exception throws with ApplicationFailure.newFailure(message, type).
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String processPayment(Order order) {
        // TODO: Move processPayment logic here. Same pattern as reserveInventory.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String dispatchToFulfillment(Order order, String reservationId) {
        // TODO: Move dispatchToFulfillment logic here.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
