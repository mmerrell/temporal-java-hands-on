package fulfillment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ad-hoc order fulfillment pipeline — the "before Temporal" version.
 *
 * Problems to spot:
 *  - Manual retry loops with Thread.sleep() — not durable, lost on crash
 *  - State in local variables — if the JVM dies after payment, we have no record
 *  - No visibility into which step we're on
 *  - Double-charge risk: payment succeeded but dispatch threw, caller retries from scratch
 */
public class FulfillmentPipeline {

    private static final Logger log = LoggerFactory.getLogger(FulfillmentPipeline.class);
    private static final int    MAX_RETRIES    = 5;
    private static final long   RETRY_DELAY_MS = 2_000;

    // State tracked in local variables — lost if the JVM crashes mid-execution
    private String reservationId;
    private String paymentConfirmation;
    private String trackingNumber;

    public OrderResult process(Order order) {
        log.info("Starting fulfillment for order {}", order.getOrderId());

        // Step 1: Reserve inventory — retry manually
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                reservationId = reserveInventory(order);
                log.info("Inventory reserved: {}", reservationId);
                break;
            } catch (Exception e) {
                log.warn("Reserve attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES)
                    throw new RuntimeException("Inventory reservation failed after " + MAX_RETRIES + " attempts", e);
                try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }

        // Step 2: Process payment — retry manually
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                paymentConfirmation = processPayment(order);
                log.info("Payment confirmed: {}", paymentConfirmation);
                break;
            } catch (Exception e) {
                log.warn("Payment attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES)
                    // Reservation already succeeded — but there's no saga to roll it back
                    throw new RuntimeException("Payment failed after " + MAX_RETRIES + " attempts", e);
                try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }

        // Step 3: Dispatch — retry manually
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                trackingNumber = dispatchToFulfillment(order, reservationId);
                log.info("Dispatched, tracking: {}", trackingNumber);
                break;
            } catch (Exception e) {
                log.warn("Dispatch attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES)
                    // Payment already charged — customer billed but order not dispatched
                    throw new RuntimeException("Dispatch failed after " + MAX_RETRIES + " attempts", e);
                try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }

        return new OrderResult(order.getOrderId(), "FULFILLED",
            reservationId, paymentConfirmation, trackingNumber);
    }

    // ── Simulated downstream calls ────────────────────────────────────────────

    private String reserveInventory(Order order) throws Exception {
        if (Math.random() < 0.3) throw new Exception("Inventory service timeout");
        return "RES-" + order.getItemSku() + "-" + System.currentTimeMillis();
    }

    private String processPayment(Order order) throws Exception {
        if (Math.random() < 0.2) throw new Exception("Payment gateway unavailable");
        return "PAY-" + order.getOrderId() + "-" + System.currentTimeMillis();
    }

    private String dispatchToFulfillment(Order order, String reservationId) throws Exception {
        if (Math.random() < 0.2) throw new Exception("Fulfillment API error");
        return "TRK-" + reservationId.hashCode() + "-" + System.currentTimeMillis();
    }
}
