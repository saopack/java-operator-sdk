package io.javaoperatorsdk.operator;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.javaoperatorsdk.operator.junit.OperatorExtension;
import io.javaoperatorsdk.operator.sample.dependenteventfiltering.ConfigMapDependentResource;
import io.javaoperatorsdk.operator.sample.dependenteventfiltering.DependentEventFilterCustomResource;
import io.javaoperatorsdk.operator.sample.dependenteventfiltering.DependentEventFilterCustomResourceSpec;
import io.javaoperatorsdk.operator.sample.dependenteventfiltering.DependentEventFilterCustomResourceTestReconciler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class DependentEventFilterIT {

  public static final String TEST = "test";
  public static final String SPEC_VAL_1 = "val1";
  public static final String SPEC_VAL_2 = "val2";

  @RegisterExtension
  OperatorExtension operator =
      OperatorExtension.builder()
          .withReconciler(new DependentEventFilterCustomResourceTestReconciler())
          .build();

  @Test
  void reconcileNotTriggeredWithDependentResourceCreateOrUpdate() {
    var resource = operator.create(DependentEventFilterCustomResource.class, createTestResource());

    await().pollDelay(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(3))
        .until(
            () -> ((DependentEventFilterCustomResourceTestReconciler) operator.getFirstReconciler())
                .getNumberOfExecutions() == 1);
    assertThat(operator.get(ConfigMap.class, TEST).getData())
        .containsEntry(ConfigMapDependentResource.KEY, SPEC_VAL_1);

    resource.getSpec().setValue(SPEC_VAL_2);
    operator.replace(DependentEventFilterCustomResource.class, resource);

    await().pollDelay(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(3))
        .until(
            () -> ((DependentEventFilterCustomResourceTestReconciler) operator.getFirstReconciler())
                .getNumberOfExecutions() == 2);
    assertThat(operator.get(ConfigMap.class, TEST).getData())
        .containsEntry(ConfigMapDependentResource.KEY, SPEC_VAL_2);
  }


  private DependentEventFilterCustomResource createTestResource() {
    DependentEventFilterCustomResource cr = new DependentEventFilterCustomResource();
    cr.setMetadata(new ObjectMeta());
    cr.getMetadata().setName(TEST);
    cr.setSpec(new DependentEventFilterCustomResourceSpec());
    cr.getSpec().setValue(SPEC_VAL_1);
    return cr;
  }

}
