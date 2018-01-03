package org.powertac.experiment.models;

import org.powertac.experiment.services.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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

  public List<Type> getSortedKeys ()
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
    for (String v : value.split(",")) {
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

  public static List<String> parseMinMaxStep (String name, String minString,
                                              String maxString, String stepString)
  {
    Type type = Type.valueOf(name.trim());
    if (type.clazz == Integer.class) {
      int min = Integer.parseInt(minString);
      int max = Integer.parseInt(maxString);
      int step = Integer.parseInt(stepString);
      return getIntValues(min, max, step);
    }
    else if (type.clazz == Double.class) {
      double min = Double.parseDouble(minString);
      double max = Double.parseDouble(maxString);
      double step = Double.parseDouble(stepString);

      String[] parts = step < Math.min(min, max) ?
          stepString.split("\\.") : minString.split("\\.");
      int digits = parts.length > 1 ? parts[1].length() : 0;

      return getDoubleValues(min, max, step, digits);
    }

    return new ArrayList<>();
  }

  private static List<String> getIntValues (int min, int max, int step)
  {
    List<String> values = new ArrayList<>();
    while (min <= max) {
      values.add(String.valueOf(min));
      min += step;
    }
    return values;
  }

  private static List<String> getDoubleValues (double min, double max,
                                               double step, int digits)
  {
    String format = IntStream.range(0, digits).mapToObj(i -> "#")
        .collect(Collectors.joining("", digits > 0 ? "#." : "#", ""));
    DecimalFormat df = new DecimalFormat(format);

    double factor = Math.pow(10, digits);
    long minScaled = Math.round(min * factor);
    long maxScaled = Math.round(max * factor);
    long stepScaled = Math.round(step * factor);

    List<String> values = new ArrayList<>();
    while (minScaled <= maxScaled) {
      values.add(df.format(minScaled / factor));
      minScaled += stepScaled;
    }
    return values;
  }
  //</editor-fold>

  public interface MapOwner
  {
    ParamMap getParamMap ();
  }
}

