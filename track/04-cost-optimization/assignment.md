---
slug: cost-optimization
id: ""
type: challenge
title: "Exercise 4: Cost Optimization with Local Activities"
teaser: Move fast in-process steps to Local Activities to cut Temporal Cloud Actions.
notes:
  - type: text
    contents: |-
      ## The Cost of Regular Activities

      In Temporal Cloud, pricing is based on **Actions** — each interaction between
      a Worker and the Temporal Server. A regular activity generates **three Actions**:

      1. `ActivityTaskScheduled` — server records the intent
      2. `ActivityTaskStarted` — worker picks it up
      3. `ActivityTaskCompleted` — worker reports the result

      For steps that run fast and in-process — input validation, fraud rules, config lookups,
      format conversion — this round-trip is unnecessary overhead, both in latency and cost.

      **Local Activities** run directly inside the Worker process. They produce a single
      `MarkerRecorded` event instead of the three-event triplet.
      Same durability guarantee. One-third the Actions cost.

      The tradeoff: local activities can't be cancelled externally, and a long-running local
      activity blocks the workflow thread. Use them for **fast, non-network steps only**.

      Hit **Start** when you're ready.
assignment: |-
  ## Exercise 4: Cost Optimization with Local Activities

  All your work is in **`FulfillmentWorkflowImpl.java`**.

  Look for the two `// TODO` blocks — one at the field declaration, one inside `processOrder()`.

  ---

  ### Part A – Create the Local Activity stub

  Replace the `null` stub with a real `LocalFulfillmentActivities` stub.

  The key differences from a regular activity stub:
  - Use `LocalActivityOptions` (not `ActivityOptions`)
  - Use `Workflow.newLocalActivityStub()` (not `Workflow.newActivityStub()`)

  ```java
  private final LocalFulfillmentActivities localActivities = Workflow.newLocalActivityStub(
      LocalFulfillmentActivities.class,
      LocalActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .build()
  );
  ```

  A 5-second timeout is appropriate — if `validateOrder` or `fraudCheck` take longer
  than 5 seconds, something is wrong.

  ---

  ### Part B – Call local activities first

  Add these two calls at the top of `processOrder()`, **before** the child workflow invocation:

  ```java
  localActivities.validateOrder(order);
  localActivities.fraudCheck(order);
  ```

  These run synchronously in the Worker process — no round-trip to the Temporal Server.

  ---

  ### Part C – Compare Event Histories

  **Terminal 1 – Worker:**
  ```
  mvn compile exec:java -Dexec.mainClass="fulfillment.FulfillmentWorker"
  ```

  **Terminal 2 – Starter:**
  ```
  mvn exec:java -Dexec.mainClass="fulfillment.Starter"
  ```

  In the **Web UI**, open `fulfillment-ORD-1004` and inspect the Event History.

  Look for:
  - `MarkerRecorded` — one event each for `validateOrder` and `fraudCheck` (local)
  - `ActivityTaskScheduled / Started / Completed` triplets — for `processPayment` and `dispatchToFulfillment` (remote)

  **Discussion:** What steps in your own workflows could be local activities?
  Think about: input validation, in-memory lookups, format conversion, fraud rules, config from a local cache.

  ---

  Click **Check** when done, or **Solve** to see the reference solution.

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
difficulty: intermediate
timelimit: 2400
enhanced_loading: null
