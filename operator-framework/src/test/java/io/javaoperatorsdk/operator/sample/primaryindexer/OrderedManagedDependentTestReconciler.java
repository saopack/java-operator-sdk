package io.javaoperatorsdk.operator.sample.primaryindexer;

import java.util.concurrent.atomic.AtomicInteger;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.support.TestExecutionInfoProvider;

@ControllerConfiguration
public class OrderedManagedDependentTestReconciler
    implements Reconciler<OrderedManagedDependentCustomResource>,
    TestExecutionInfoProvider {

  private final AtomicInteger numberOfExecutions = new AtomicInteger(0);


  @Override
  public UpdateControl<OrderedManagedDependentCustomResource> reconcile(
      OrderedManagedDependentCustomResource resource,
      Context<OrderedManagedDependentCustomResource> context) {
    numberOfExecutions.addAndGet(1);
    return UpdateControl.noUpdate();
  }

  public int getNumberOfExecutions() {
    return numberOfExecutions.get();
  }

}
