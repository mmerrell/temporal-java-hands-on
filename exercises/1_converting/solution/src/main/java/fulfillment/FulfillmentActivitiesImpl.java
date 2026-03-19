package fulfillment;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FulfillmentActivitiesImpl implements FulfillmentActivities {

    private static final Logger log = LoggerFactory.getLogger(FulfillmentActivitiesImpl.class);

    @Override
    public String reserveInventory(Order order) {
        log.info("Reserving inventory for SKU {} qty {}", order.getItemSku(), order.getQuantity());
        if (Math.random() < 0.3)
            throw ApplicationFailure.newFailure("Inventory service timeout", "InventoryError");
        return "RES-" + order.getItemSku() + "-" + System.currentTimeMillis();
    }

    @Override
    public String processPayment(Order order) {
        log.info("Processing payment ${} for {}", order.getTotalAmount(), order.getOrderId());
        if (Math.random() < 0.2)
            throw ApplicationFailure.newFailure("Payment gateway unavailable", "PaymentError");
        return "PAY-" + order.getOrderId() + "-" + System.currentTimeMillis();
    }

    @Override
    public String dispatchToFulfillment(Order order, String reservationId) {
        log.info("Dispatching order {} reservation {}", order.getOrderId(), reservationId);
        if (Math.random() < 0.2)
            throw ApplicationFailure.newFailure("Fulfillment API error", "DispatchError");
        return "TRK-" + reservationId.hashCode() + "-" + System.currentTimeMillis();
    }
}
