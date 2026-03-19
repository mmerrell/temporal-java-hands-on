package fulfillment;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class FulfillmentWorkflowImpl implements FulfillmentWorkflow {

    private static final Logger log = Workflow.getLogger(FulfillmentWorkflowImpl.class);

    // TODO Part A: Create ActivityOptions with a StartToCloseTimeout of 30 seconds.
    //              Then create a FulfillmentActivities stub via Workflow.newActivityStub(...).
    private final FulfillmentActivities activities = null; // replace this

    @Override
    public OrderResult processOrder(Order order) {
        log.info("Processing order {}", order.getOrderId());

        // TODO Part B: Call each activity in sequence:
        //   1. activities.reserveInventory(order)                    → reservationId
        //   2. activities.processPayment(order)                      → paymentConfirmation
        //   3. activities.dispatchToFulfillment(order, reservationId) → trackingNumber
        //   Return an OrderResult with status "FULFILLED".

        return null; // replace this
    }
}
