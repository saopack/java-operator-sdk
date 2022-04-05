package io.javaoperatorsdk.operator.sample.dependenteventfiltering;

public class DependentEventFilterCustomResourceSpec {

  private String value;

  public String getValue() {
    return value;
  }

  public DependentEventFilterCustomResourceSpec setValue(String value) {
    this.value = value;
    return this;
  }
}
