## Exercise 3: Parallel Activities

During this exercise, you will:

- Fan out multiple activity calls concurrently using `Async.function()`
- Collect results with `Promise.allOf()` to wait for all completions
- Return the first successful warehouse reservation

This directly mirrors Coupang's scale challenge: checking all warehouses simultaneously
instead of sequentially. The same pattern applies to processing thousands of containers
in parallel.

## Setup

```bash
temporal server start-dev
```

`cd` into `3_parallel_activities/practice/`.

## Part A: Fan out with Async.function()

Open `InventoryReservationWorkflowImpl.java`. For each `warehouseId` in `WAREHOUSES`:

```java
Promise<String> p = Async.function(
    warehouseActivities::checkWarehouseInventory,
    warehouseId, sku, quantity
);
promises.add(p);
```

`Async.function()` is Temporal's opt-in to concurrency — Java SDK is synchronous by default.

## Part B: Wait with Promise.allOf()

```java
Promise.allOf(promises).get();
```

This blocks the workflow durably until every promise resolves.

## Part C: Return the first success

Iterate over `promises`, call `.get()` on each, return the first non-null value.
If all null, throw a non-retryable `ApplicationFailure` with type `"OutOfStock"`.

## Part D: Run It

```bash
# Terminal 1
mvn compile exec:java -Dexec.mainClass="fulfillment.FulfillmentWorker"

# Terminal 2
mvn exec:java -Dexec.mainClass="fulfillment.Starter"
```

In the Web UI, open `inventory-ORD-1003`. Notice the three `checkWarehouseInventory`
activity tasks scheduled nearly simultaneously — compare with Exercise 2 where they
ran one at a time.

### This is the end of the exercise.
