package fulfillment;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WarehouseActivitiesImpl implements WarehouseActivities {

    private static final Logger log = LoggerFactory.getLogger(WarehouseActivitiesImpl.class);

    // Simulated network latency per warehouse (milliseconds)
    private static final Map<String, Long> LATENCY = new HashMap<>();
    // Stock availability — only the last two have inventory
    private static final Map<String, Boolean> STOCK = new HashMap<>();

    static {
        LATENCY.put("WH-INCHEON",   800L);
        LATENCY.put("WH-BUCHEON",  2200L);
        LATENCY.put("WH-DAEJEON",  1500L);
        LATENCY.put("WH-BUSAN",    3000L);
        LATENCY.put("WH-GWANGJU",  1800L);
        LATENCY.put("WH-SEJONG",   2500L);

        STOCK.put("WH-INCHEON",  false);
        STOCK.put("WH-BUCHEON",  false);
        STOCK.put("WH-DAEJEON",  false);
        STOCK.put("WH-BUSAN",    false);
        STOCK.put("WH-GWANGJU",  true);
        STOCK.put("WH-SEJONG",   true);
    }

    @Override
    public String checkWarehouseInventory(String warehouseId, String sku, int quantity) {
        log.info("Checking {} at warehouse {} (simulated latency: {}ms)",
            sku, warehouseId, LATENCY.getOrDefault(warehouseId, 1000L));

        // Simulate network latency
        try {
            Thread.sleep(LATENCY.getOrDefault(warehouseId, 1000L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Occasional transient failure
        if (Math.random() < 0.1)
            throw ApplicationFailure.newFailure("Warehouse " + warehouseId + " API timeout", "WarehouseError");

        Boolean inStock = STOCK.getOrDefault(warehouseId, false);
        if (!inStock) {
            log.info("Warehouse {} — out of stock", warehouseId);
            return null;
        }

        return "RES-" + warehouseId + "-" + sku + "-" + System.currentTimeMillis();
    }
}
