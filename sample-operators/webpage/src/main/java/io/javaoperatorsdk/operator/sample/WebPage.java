package io.javaoperatorsdk.operator.sample;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("sample.javaoperatorsdk")
@Version("v1")
public class WebPage extends CustomResource<WebPageSpec, WebPageStatus>
    implements Namespaced {

  @Override
  protected WebPageStatus initStatus() {
    return new WebPageStatus();
  }

  @Override
  public String toString() {
    return "WebPage{" +
        "spec=" + spec +
        ", status=" + status +
        '}';
  }
}
