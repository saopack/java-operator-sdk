package io.javaoperatorsdk.operator.processing.dependent.workflow.condition;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;

public interface ReconcileCondition<P extends HasMetadata> {

  boolean isMet(DependentResource<?, ?> dependentResource, P primary, Context<P> context);

}