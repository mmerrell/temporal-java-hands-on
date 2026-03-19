package fulfillment;

import io.temporal.activity.ActivityOptions;
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

    @Override
    public OrderResult processOrder(Order order) {
        log.info("Processing order {}", order.getOrderId());

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
