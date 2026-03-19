---
slug: converting
id: ""
type: challenge
title: "Exercise 1: Converting a Workflow"
teaser: Replace a fragile retry loop with Temporal Activities and a durable Workflow.
notes:
  - type: text
    contents: |-
      ## The Problem with Manual Retry Loops

      The starting point for this exercise is `FulfillmentPipeline.java` — a deliberately fragile
      implementation using `Thread.sleep()` retries and local variable state.

      If the process dies mid-execution, the order is lost. If a step fails after three attempts,
      the whole pipeline crashes. Retry behavior is hardcoded and invisible.

      **Temporal fixes all of this.** Your job is to move the logic into proper
      Activity implementations and wire them together in a Workflow.

      Hit **Start** when you're ready.
assignment: |-
  ## Exercise 1: Converting a Workflow

  Open **`FulfillmentActivitiesImpl.java`** and **`FulfillmentWorkflowImpl.java`** in the editor.
  Look for `// TODO` comments — they mark everything you need to implement.

  ---

  ### Part A – Implement the three Activities

  In `FulfillmentActivitiesImpl.java`, fill in each of the three methods:

  1. `reserveInventory(Order order)` — move the logic from `FulfillmentPipeline.java`
  2. `processPayment(Order order)` — same pattern
  3. `dispatchToFulfillment(Order order, String reservationId)` — same pattern

  For each method:
  - Replace the `throw new UnsupportedOperationException(...)` stub
  - Replace raw `throw new Exception(...)` calls with `ApplicationFailure.newFailure(message, type)`
  - Keep the `Math.random()` failure simulation — we want retries to happen

  ---

  ### Part B – Implement the Workflow

  In `FulfillmentWorkflowImpl.java`:

  1. Create `ActivityOptions` with a `StartToCloseTimeout` of **30 seconds**
  2. Create a `FulfillmentActivities` stub via `Workflow.newActivityStub(...)`
  3. Replace the `null` stub for `activities`
  4. In `processOrder()`, call the three activities in sequence and return an `OrderResult`

  ---

  ### Part C – Run it

  Once your code compiles (`mvn compile` in either terminal), start both processes:

  **Terminal 1 – Worker:**
  ```
  mvn compile exec:java -Dexec.mainClass="fulfillment.FulfillmentWorker"
  ```

  **Terminal 2 – Starter:**
  ```
  mvn exec:java -Dexec.mainClass="fulfillment.Starter"
  ```

  Open the **Temporal Web UI** tab and find workflow `fulfillment-ORD-1001`.
  Try killing the Worker mid-execution (Ctrl+C in Terminal 1) and restarting it — what happens?

  ---

  ### Check your work

  Click **Check** when you think you're done. The checker will verify your source code
  and confirm the project compiles.

  > **Stuck?** Click **Solve** to apply the reference solution and see a working implementation.

tabs:
  - type: terminal
    title: Terminal 1 – Worker
    hostname: sandbox
    path: /home/user/exercise
  - type: terminal
    title: Terminal 2 – Starter
    hostname: sandbox
    path: /home/user/exercise
  - type: service
    title: Temporal Web UI
    hostname: temporal-server
    port: 8233
    path: /
difficulty: basic
timelimit: 2400
enhanced_loading: null
