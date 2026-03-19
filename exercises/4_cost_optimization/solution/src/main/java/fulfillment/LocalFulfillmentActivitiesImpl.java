package fulfillment;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFulfillmentActivitiesImpl implements LocalFulfillmentActivities {

    private static final Logger log = LoggerFactory.getLogger(LocalFulfillmentActivitiesImpl.class);

    @Override
    public void validateOrder(Order order) {
        log.info("Validating order {}", order.getOrderId());
        if (order.getQuantity() <= 0)
            throw ApplicationFailure.newNonRetryableFailure("Quantity must be > 0", "ValidationError");
        if (order.getTotalAmount() <= 0)
            throw ApplicationFailure.newNonRetryableFailure("Amount must be > 0", "ValidationError");
    }

    @Override
    public String fraudCheck(Order order) {
        log.info("Fraud check for customer {}", order.getCustomerId());
        if (order.getTotalAmount() > 10_000)
            throw ApplicationFailure.newNonRetryableFailure(
                "Order flagged by fraud check", "FraudError");
        return "CLEARED";
    }
}
