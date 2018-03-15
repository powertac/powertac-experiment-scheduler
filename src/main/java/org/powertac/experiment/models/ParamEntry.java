package org.powertac.experiment.models;

import org.apache.log4j.Logger;
import org.powertac.experiment.services.Utils;

import java.util.Iterator;
import java.util.List;


public class ParamEntry
{
  private String name;
  private String value;

  public ParamEntry (String name, String value)
  {
    this.name = name;
    this.value = value;
  }

  public String getName ()
  {
    return name.trim();
  }

  public void setName (String name)
  {
    this.name = name;
  }

  public String getValue ()
  {
    return value.trim();
  }

  public void setValue (String value)
  {
    this.value = value;
  }

  public static ParamMap createParamMap (List<ParamEntry> paramList, Logger log)
  {
    ParamMap paramMap = new ParamMap();

    Iterator<ParamEntry> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      ParamEntry entry = iterator.next();
      String name = entry.getName();
      String value = entry.getValue();

      if (name.isEmpty() && value.isEmpty()) {
        iterator.remove();
        continue;
      }

      if (value.isEmpty()) {
        iterator.remove();
        log.warn("Ignoring parameter " + name + ", value is empty");
        Utils.growlMessage("Ignoring parameter " + name + ", value is empty");
      }
      else if (name.isEmpty()) {
        iterator.remove();
        log.warn("Ignoring parameter, name is empty : value is " + value);
        Utils.growlMessage("Ignoring parameter, name is empty : value is " + value);
      }

      paramMap.put(name, new Parameter(null, name, value));
    }

    return paramMap;
  }

  @Override
  public String toString ()
  {
    return name + " : " + value;
  }
}
