package fulfillment;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface InventoryReservationWorkflow {
    @WorkflowMethod
    String reserve(String sku, int quantity);
}
