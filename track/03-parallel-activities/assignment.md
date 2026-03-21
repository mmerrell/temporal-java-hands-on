---
slug: parallel-activities
id: jxuystyteaj8
type: challenge
title: 'Exercise 3: Parallel Activities'
teaser: Fan out warehouse checks concurrently with Async.function() and Promise.allOf().
notes:
- type: text
  contents: |-
    In Exercise 2, the inventory child workflow checks warehouses **one at a time**.
    If each `checkWarehouseInventory` call takes 500ms and you have 6 warehouses,
    that's 3 seconds minimum — even if the first warehouse has stock.

    **The parallel pattern checks all warehouses simultaneously.**
    At Coupang's scale, this isn't an optimization — it's a requirement.
    The same fan-out pattern applies anywhere you're processing many independent
    items: containers, SKUs, payment methods, notification channels.

    The Java SDK is **synchronous by default** — you have to explicitly opt in
    to concurrency using `Async.function()`. This makes concurrent code readable
    and keeps it deterministic for replay.

    Hit **Start** when you're ready.
tabs:
- id: xscxon9ztytk
  title: Terminal 1 – Worker
  type: terminal
  hostname: sandbox
  workdir: /home/user/exercise
- id: omgwavx03gl8
  title: Terminal 2 – Starter
  type: terminal
  hostname: sandbox
  workdir: /home/user/exercise
- id: yj4oa85lxsws
  title: Temporal Web UI
  type: service
  hostname: temporal-server
  path: /
  port: 8233
difficulty: intermediate
timelimit: 2400
enhanced_loading: null
---

## Exercise 3: Parallel Activities

All your work is in **`InventoryReservationWorkflowImpl.java`**.
The activity stub is already wired up — focus on the `reserve()` method.

Files are in `/home/user/exercise/src/main/java/fulfillment/`.

***

### Part A – Fan out with Async.function()

For each `warehouseId` in `WAREHOUSES`, launch the activity concurrently:

```java
for (String warehouseId : WAREHOUSES) {
    Promise<String> p = Async.function(
        warehouseActivities::checkWarehouseInventory,
        warehouseId, sku, quantity
    );
    promises.add(p);
}
```

`Async.function()` schedules the activity without blocking here.
All six `checkWarehouseInventory` calls are in-flight simultaneously.

***

### Part B – Wait with Promise.allOf()

```java
Promise.allOf(promises).get();
```

This blocks the workflow durably until **every** promise resolves.
If the worker restarts mid-wait, Temporal replays the promises from history.

***

### Part C – Return the first success

```java
for (Promise<String> p : promises) {
    String result = p.get();
    if (result != null) {
        return result;
    }
}
throw ApplicationFailure.newNonRetryableFailure("No stock available", "OutOfStock");
```

***

### Part D – Run it and compare

**Terminal 1 – Worker:**
```
mvn compile exec:java -Dexec.mainClass="fulfillment.FulfillmentWorker"
```

**Terminal 2 – Starter:**
```
mvn exec:java -Dexec.mainClass="fulfillment.Starter"
```

In the **Web UI**, open `inventory-ORD-1003`. Look at the Event History —
all six `ActivityTaskScheduled` events appear nearly simultaneously.
Compare with Exercise 2 where they were staggered.

***

Click **Check** when done, or **Solve** to see the reference solution.
