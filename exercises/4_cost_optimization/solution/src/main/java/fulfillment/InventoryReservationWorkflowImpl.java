package fulfillment;

import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryReservationWorkflowImpl implements InventoryReservationWorkflow {

    private static final Logger log = Workflow.getLogger(InventoryReservationWorkflowImpl.class);

    private static final List<String> WAREHOUSES = Arrays.asList(
        "WH-INCHEON", "WH-BUCHEON", "WH-DAEJEON",
        "WH-BUSAN",   "WH-GWANGJU", "WH-SEJONG"
    );

    private final WarehouseActivities warehouseActivities = Workflow.newActivityStub(
        WarehouseActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .build()
    );

    @Override
    public String reserve(String sku, int quantity) {
        log.info("Checking {} warehouses in parallel for SKU {}", WAREHOUSES.size(), sku);

        List<Promise<String>> promises = new ArrayList<>();
        for (String warehouseId : WAREHOUSES) {
            Promise<String> p = Async.function(
                warehouseActivities::checkWarehouseInventory,
                warehouseId, sku, quantity
            );
            promises.add(p);
        }

        Promise.allOf(promises).get();

        for (Promise<String> p : promises) {
            String reservationId = p.get();
            if (reservationId != null) {
                log.info("Reservation found: {}", reservationId);
                return reservationId;
            }
        }

        throw ApplicationFailure.newNonRetryableFailure(
            "No warehouse has stock for SKU " + sku, "OutOfStock");
    }
}
