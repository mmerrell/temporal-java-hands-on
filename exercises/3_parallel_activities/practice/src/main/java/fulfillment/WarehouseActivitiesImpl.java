package fulfillment;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class WarehouseActivitiesImpl implements WarehouseActivities {

    private static final Logger log = LoggerFactory.getLogger(WarehouseActivitiesImpl.class);

    private static final Map<String, Boolean> STOCK = new HashMap<>();
    static {
        STOCK.put("WH-INCHEON", true);
        STOCK.put("WH-BUCHEON", false);
        STOCK.put("WH-DAEJEON", true);
    }

    @Override
    public String checkWarehouseInventory(String warehouseId, String sku, int quantity) {
        log.info("Checking {} at warehouse {}", sku, warehouseId);
        if (Math.random() < 0.15)
            throw ApplicationFailure.newFailure("Warehouse " + warehouseId + " API timeout", "WarehouseError");
        Boolean inStock = STOCK.getOrDefault(warehouseId, false);
        if (!inStock) return null;
        return "RES-" + warehouseId + "-" + sku + "-" + System.currentTimeMillis();
    }
}
