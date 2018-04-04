package org.powertac.experiment.models;

import org.apache.log4j.Logger;
import org.powertac.experiment.services.Utils;

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

    for (ParamEntry entry : paramList) {
      String name = entry.getName();
      String value = entry.getValue();

      // Both empty, ignore line completely
      if (name.isEmpty() && value.isEmpty()) {
        continue;
      }
      if (value.isEmpty()) {
        String msg = "Ignoring parameter " + name + ", value is empty";
        log.warn(msg);
        Utils.growlMessage("Warning", msg);
      }
      else if (name.isEmpty()) {
        String msg = "Ignoring parameter, name is empty : value is " + value;
        log.warn(msg);
        Utils.growlMessage("Warning", msg);
      }
      else {
        paramMap.put(name, new Parameter(null, name, value));
      }
    }

    return paramMap;
  }

  @Override
  public String toString ()
  {
    return name + " : " + value;
  }
}
