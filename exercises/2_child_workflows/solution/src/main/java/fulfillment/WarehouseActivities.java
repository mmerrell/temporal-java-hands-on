package fulfillment;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface WarehouseActivities {
    /** Returns a reservation ID if this warehouse has stock, null otherwise. */
    @ActivityMethod
    String checkWarehouseInventory(String warehouseId, String sku, int quantity);
}
