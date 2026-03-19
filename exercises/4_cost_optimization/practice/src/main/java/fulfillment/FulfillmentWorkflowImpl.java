package fulfillment;

import io.temporal.activity.ActivityOptions;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class FulfillmentWorkflowImpl implements FulfillmentWorkflow {

    private static final Logger log = Workflow.getLogger(FulfillmentWorkflowImpl.class);

    private final FulfillmentActivities activities = Workflow.newActivityStub(
        FulfillmentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .build()
    );

    // TODO Part A: Create a LocalFulfillmentActivities stub using Workflow.newLocalActivityStub().
    //   Use LocalActivityOptions (not ActivityOptions) with a StartToCloseTimeout of 5 seconds.
    //   Local activities run in-process — no round-trip to the Temporal Server for scheduling.
    private final LocalFulfillmentActivities localActivities = null; // replace this

    @Override
    public OrderResult processOrder(Order order) {
        log.info("Processing order {}", order.getOrderId());

        // TODO Part B: Call localActivities.validateOrder(order) and
        //   localActivities.fraudCheck(order) BEFORE the child workflow invocation.
        //   These are fast in-process checks — no reason to pay for a Server round-trip.

        InventoryReservationWorkflow inventoryWorkflow = Workflow.newChildWorkflowStub(
            InventoryReservationWorkflow.class,
            ChildWorkflowOptions.newBuilder()
                .setWorkflowId("inventory-" + order.getOrderId())
                .build()
        );
        String reservationId = inventoryWorkflow.reserve(order.getItemSku(), order.getQuantity());

        String paymentConfirmation = activities.processPayment(order);
        String trackingNumber      = activities.dispatchToFulfillment(order, reservationId);

        return new OrderResult(order.getOrderId(), "FULFILLED",
            reservationId, paymentConfirmation, trackingNumber);
    }
}
