---
slug: child-workflows
id: uo7y4q9oaxf1
type: challenge
title: 'Exercise 2: Child Workflows'
teaser: Decompose inventory reservation into a dedicated child workflow with its own
  history.
notes:
- type: text
  contents: |-
    In Exercise 1, inventory reservation was just an activity in the main workflow.
    That works, but it means the reservation logic shares history with payment and dispatch.

    **Child workflows give you:**
    - A **separate Event History** — the parent's history stays lean
    - An **independent retry boundary** — the child can fail and be retried without restarting the parent
    - A **meaningful workflow ID** you can query or signal independently (e.g., `inventory-ORD-1002`)

    This pattern is common at companies running Temporal at scale — complex sub-processes
    become first-class workflows rather than buried activity chains.

    Hit **Start** when you're ready.
tabs:
- id: 9w5vulsv78xr
  title: Terminal 1 – Worker
  type: terminal
  hostname: sandbox
  workdir: /home/user/exercise
- id: isksn9ka25v9
  title: Terminal 2 – Starter
  type: terminal
  hostname: sandbox
  workdir: /home/user/exercise
- id: u8avwbdct35o
  title: Temporal Web UI
  type: service
  hostname: temporal-server
  path: /
  port: 8233
difficulty: basic
timelimit: 2400
enhanced_loading: null
---

## Exercise 2: Child Workflows

You're working with two implementation files this time:
- **`InventoryReservationWorkflowImpl.java`** — the child workflow (new)
- **`FulfillmentWorkflowImpl.java`** — the parent workflow (calls the child)

Files are in `/home/user/exercise/src/main/java/fulfillment/`. Look for `// TODO` comments in both.

***

### Part A – Create the WarehouseActivities stub

In `InventoryReservationWorkflowImpl.java`, replace the `null` stub:

```java
private final WarehouseActivities warehouseActivities = Workflow.newActivityStub(
    WarehouseActivities.class,
    ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(15))
        .build()
);
```

***

### Part B – Implement the child workflow logic

In `reserve()`, iterate over `WAREHOUSES` and call `checkWarehouseInventory` for each.
Return the **first non-null** result (first warehouse with stock wins).

If all warehouses return null, throw a **non-retryable** `ApplicationFailure`:
```java
throw ApplicationFailure.newNonRetryableFailure("No stock available", "OutOfStock");
```

***

### Part C – Call the child from the parent

In `FulfillmentWorkflowImpl.java`, replace the `null` stub for `reservationId`:
```java
InventoryReservationWorkflow inventoryWorkflow = Workflow.newChildWorkflowStub(
    InventoryReservationWorkflow.class,
    ChildWorkflowOptions.newBuilder()
        .setWorkflowId("inventory-" + order.getOrderId())
        .build()
);
String reservationId = inventoryWorkflow.reserve(order.getItemSku(), order.getQuantity());
```

***

### Part D – Run it

**Terminal 1 – Worker:**
```
mvn compile exec:java -Dexec.mainClass="fulfillment.FulfillmentWorker"
```

**Terminal 2 – Starter:**
```
mvn exec:java -Dexec.mainClass="fulfillment.Starter"
```

In the **Web UI**, find both `fulfillment-ORD-1002` and `inventory-ORD-1002`.
Click into each — notice the child has its own separate Event History.

***

Click **Check** when done, or **Solve** to see the reference solution.
