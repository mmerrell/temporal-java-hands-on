package fulfillment;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FulfillmentActivities {

    @ActivityMethod
    String processPayment(Order order);

    @ActivityMethod
    String dispatchToFulfillment(Order order, String reservationId);
}
