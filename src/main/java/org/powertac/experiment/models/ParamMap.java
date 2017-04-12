package org.powertac.experiment.models;

import org.powertac.experiment.services.Utils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ParamMap
{
  private MapOwner parent;
  private Map<Type, Parameter> map;

  public ParamMap (MapOwner parent, Map<Type, Parameter> paramMap)
  {
    this.parent = parent;
    if (paramMap != null) {
      this.map = paramMap;
    }
    else {
      this.map = new HashMap<>();
    }
  }

  public ParamMap ()
  {
    this(null, null);
  }

  public void createParameter (Type type, Object value)
  {
    Parameter parameter = new Parameter(parent, type, value);
    map.put(type, parameter);
  }

  public String getValue (Type type)
  {
    Parameter param = map.get(type);
    if (param != null) {
      return param.getValue();
    }
    else {
      return type.getDefault();
    }
  }

  public int getPomId ()
  {
    Parameter param = map.get(Type.pomId);
    try {
      return Integer.valueOf(param.getValue());
    }
    catch (Exception ignored) {
      return -1;
    }
  }

  public Date getStartTime ()
  {
    Parameter startTime = map.get(Type.startTime);
    try {
      return Utils.dateFromFull(startTime.getValue());
    }
    catch (Exception ignored) {
      return Utils.offsetDate();
    }
  }

  public void setStartTime (Date date)
  {
    Parameter startTime = map.get(Type.startTime);
    String newValue = Utils.dateToStringFull(date);
    if (startTime == null) {
      startTime = new Parameter(this, Type.startTime, newValue);
    }
    else {
      startTime.setValue(newValue);
    }

    map.put(Type.startTime, startTime);
  }

  public void setOrUpdateValue (Type type, String value, boolean exclusive)
  {
    Parameter param = map.get(type);
    if (param == null) {
      map.put(type, new Parameter(parent, type, value));
    }
    else if (exclusive) {
      param.setValue(value);
    }
    else {
      param.setValue(param.getValue() + "," + value);
    }
  }

  public void setOrUpdateValue (Type type, String value)
  {
    setOrUpdateValue(type, value, true);
  }

  public List<Type> getSortedKeys()
  {
    List<Type> keys = new ArrayList<>(map.keySet());
    keys.sort(Comparator.comparing(Enum::toString));
    return keys;
  }

  //<editor-fold desc="Proxy methods for the map">
  public Parameter get (Type type)
  {
    return map.get(type);
  }

  public void put (Type type, Parameter parameter)
  {
    map.put(type, parameter);
  }

  public Set<Type> keySet ()
  {
    return map.keySet();
  }

  public Collection<Parameter> values ()
  {
    return map.values();
  }

  public Set<Map.Entry<Type, Parameter>> entrySet ()
  {
    return map.entrySet();
  }

  public Parameter remove (Type type)
  {
    return map.remove(type);
  }

  @Override
  public String toString ()
  {
    return map.toString();
  }
  //</editor-fold>

  //<editor-fold desc="Methods for Variable">
  public static List<String> validateVariable (String name, String value)
  {
    List<String> messages = new ArrayList<>();
    if (name.isEmpty() || value.isEmpty()) {
      Utils.growlMessage("Warn",
          "No variable given, there will only be 1 experiment!");
      return messages;
    }

    // Check if variableName is allowed
    Type variable;
    try {
      variable = Type.valueOf(name);
    }
    catch (Exception ignored) {
      messages.add("Variable name isn't an allowed type!");
      return messages;
    }

    // Check if variableValues are allowed
    for (String v : getValueList(name, value)) {
      try {
        if (variable.clazz == Integer.class) {
          Integer.parseInt(v);
        }
        else if (variable.clazz == Double.class) {
          Double.parseDouble(v);
        }
      }
      catch (Exception ignored) {
        messages.add("Variable format isn't correct");
        break;
      }
    }

    return messages;
  }

  public static List<String> getValueList (String name, String value)
  {
    value = value.replace(" ", "");

    // A comma separated list of values
    if (StringUtils.countOccurrencesOf(value, "-") != 1) {
      return Arrays.asList(value.split(","));
    }

    // A min-max,step format
    if (StringUtils.countOccurrencesOf(value, ",") == 1 &&
        StringUtils.countOccurrencesOf(value, "-") == 1) {
      Type type = Type.valueOf(name);
      if (type.clazz == Integer.class) {
        return getIntValues(value);
      }
      else if (type.clazz == Double.class) {
        return getDoubleValues(value);
      }
    }

    return new ArrayList<>();
  }

  private static List<String> getIntValues (String variableValue)
  {
    int min = Integer.parseInt(variableValue.split("-")[0]);
    String tail = variableValue.split("-")[1];
    int max = Integer.parseInt(tail.split(",")[0]);
    int step = Integer.parseInt(tail.split(",")[1]);

    List<String> values = new ArrayList<>();
    while (min <= max) {
      values.add(String.valueOf(min));
      min += step;
    }
    return values;
  }

  private static List<String> getDoubleValues (String variableValue)
  {
    double min = Double.parseDouble(variableValue.split("-")[0]);
    String tail = variableValue.split("-")[1];
    double max = Double.parseDouble(tail.split(",")[0]);
    double step = Double.parseDouble(tail.split(",")[1]);

    List<String> values = new ArrayList<>();
    while (min <= max) {
      values.add(String.valueOf(min));
      min += step;
    }
    return values;
  }
  //</editor-fold>

  public interface MapOwner
  {
    ParamMap getParamMap ();
  }
}

