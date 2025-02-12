package io.javaoperatorsdk.operator;

import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;
import io.javaoperatorsdk.operator.sample.simple.TestCustomReconciler;
import io.javaoperatorsdk.operator.sample.simple.TestCustomResource;

import static io.javaoperatorsdk.operator.ReconcilerUtils.getDefaultFinalizerName;
import static io.javaoperatorsdk.operator.ReconcilerUtils.getDefaultNameFor;
import static io.javaoperatorsdk.operator.ReconcilerUtils.getDefaultReconcilerName;
import static io.javaoperatorsdk.operator.ReconcilerUtils.handleKubernetesClientException;
import static io.javaoperatorsdk.operator.ReconcilerUtils.isFinalizerValid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReconcilerUtilsTest {

  @Test
  void defaultReconcilerNameShouldWork() {
    assertEquals(
        "testcustomreconciler",
        getDefaultReconcilerName(TestCustomReconciler.class.getCanonicalName()));
    assertEquals(
        getDefaultNameFor(TestCustomReconciler.class),
        getDefaultReconcilerName(TestCustomReconciler.class.getCanonicalName()));
    assertEquals(
        getDefaultNameFor(TestCustomReconciler.class),
        getDefaultReconcilerName(TestCustomReconciler.class.getSimpleName()));
  }

  @Test
  void defaultFinalizerShouldWork() {
    assertTrue(isFinalizerValid(getDefaultFinalizerName(Pod.class)));
    assertTrue(isFinalizerValid(getDefaultFinalizerName(TestCustomResource.class)));
  }

  @Test
  void equalsSpecObject() {
    var d1 = createTestDeployment();
    var d2 = createTestDeployment();

    assertThat(ReconcilerUtils.specsEqual(d1, d2)).isTrue();
  }

  @Test
  void equalArbitraryDifferentSpecsOfObjects() {
    var d1 = createTestDeployment();
    var d2 = createTestDeployment();
    d2.getSpec().getTemplate().getSpec().setHostname("otherhost");

    assertThat(ReconcilerUtils.specsEqual(d1, d2)).isFalse();
  }

  @Test
  void getsSpecWithReflection() {
    Deployment deployment = new Deployment();
    deployment.setSpec(new DeploymentSpec());
    deployment.getSpec().setReplicas(5);

    DeploymentSpec spec = (DeploymentSpec) ReconcilerUtils.getSpec(deployment);
    assertThat(spec.getReplicas()).isEqualTo(5);
  }

  @Test
  void setsSpecWithReflection() {
    Deployment deployment = new Deployment();
    deployment.setSpec(new DeploymentSpec());
    deployment.getSpec().setReplicas(5);
    DeploymentSpec newSpec = new DeploymentSpec();
    newSpec.setReplicas(1);

    ReconcilerUtils.setSpec(deployment, newSpec);

    assertThat(deployment.getSpec().getReplicas()).isEqualTo(1);
  }

  private Deployment createTestDeployment() {
    Deployment deployment = new Deployment();
    deployment.setSpec(new DeploymentSpec());
    deployment.getSpec().setReplicas(5);
    PodTemplateSpec podTemplateSpec = new PodTemplateSpec();
    deployment.getSpec().setTemplate(podTemplateSpec);
    podTemplateSpec.setSpec(new PodSpec());
    podTemplateSpec.getSpec().setHostname("localhost");
    return deployment;
  }

  @Test
  void handleKubernetesExceptionShouldThrowMissingCRDExceptionWhenAppropriate() {
    assertThrows(MissingCRDException.class, () -> handleKubernetesClientException(
        new KubernetesClientException(
            "Failure executing: GET at: https://kubernetes.docker.internal:6443/apis/tomcatoperator.io/v1/tomcats. Message: Not Found.",
            404, null),
        HasMetadata.getFullResourceName(Tomcat.class)));
  }

  @Group("tomcatoperator.io")
  @Version("v1")
  @ShortNames("tc")
  private static class Tomcat extends CustomResource<Void, Void> implements Namespaced {

  }
}
