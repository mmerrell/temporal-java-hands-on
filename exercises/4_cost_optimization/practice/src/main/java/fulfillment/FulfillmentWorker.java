package fulfillment;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class FulfillmentWorker {
    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker(Constants.TASK_QUEUE_NAME);
        worker.registerWorkflowImplementationTypes(
            FulfillmentWorkflowImpl.class,
            InventoryReservationWorkflowImpl.class
        );
        worker.registerActivitiesImplementations(
            new FulfillmentActivitiesImpl(),
            new LocalFulfillmentActivitiesImpl(),
            new WarehouseActivitiesImpl()
        );

        factory.start();
        System.out.println("Worker started on task queue: " + Constants.TASK_QUEUE_NAME);
    }
}
