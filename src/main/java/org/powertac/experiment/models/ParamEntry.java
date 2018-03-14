package org.powertac.experiment.models;

public class ParamEntry
{
  private String name;
  private String value;

  public ParamEntry (String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName (String name)
  {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue (String value)
  {
    this.value = value;
  }

  public boolean isEmpty ()
  {
    return name.trim().isEmpty() && value.trim().isEmpty();
  }

  @Override
  public String toString ()
  {
    return name + " : " + value;
  }
}
