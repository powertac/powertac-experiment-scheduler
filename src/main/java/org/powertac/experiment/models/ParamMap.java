package org.powertac.experiment.models;

import org.powertac.experiment.beans.Location;
import org.powertac.experiment.services.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
  private Map<String, Parameter> map;

  public ParamMap (MapOwner parent, Map<String, Parameter> paramMap)
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

  public void createParameter (String type, Object value)
  {
    Parameter parameter = new Parameter(parent, type, value);
    map.put(type, parameter);
  }

  public String getValue (String name)
  {
    Parameter param = map.get(name);
    if (param != null) {
      return param.getValue();
    }
    else {
      return Type.get(getPomId(), name).getDefault();
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

  public boolean getReuseBoot ()
  {
    Parameter reuseBoot = map.get(Type.reuseBoot);
    try {
      return Boolean.valueOf(reuseBoot.getValue());
    }
    catch (Exception ignored) {
      return true;
    }
  }

  public void setStartTime (Date date)
  {
    Parameter startTime = map.get(Type.startTime().name);
    String newValue = Utils.dateToStringFull(date);
    if (startTime == null) {
      startTime = new Parameter(this, Type.startTime, newValue);
    }
    else {
      startTime.setValue(newValue);
    }

    map.put(Type.startTime().name, startTime);
  }

  public void setOrUpdateValue (String type, String value)
  {
    Parameter param = map.get(type);
    if (param == null) {
      map.put(type, new Parameter(parent, type, value));
    }
    else {
      param.setValue(value);
    }
  }

  public List<String> getSortedKeys ()
  {
    List<String> keys = new ArrayList<>(map.keySet());
    Collections.sort(keys);
    return keys;
  }

  public List<String> validateStudyMap (String variableName)
  {
    List<String> messages = new ArrayList<>();
    checkValidPomId(messages);
    checkValidMultiplier(messages);
    checkValidBrokers(messages, variableName);
    checkValidLocation(messages, variableName);

    // TODO Check simStartDate, bootstrapId, gameLength ??

    return messages;
  }

  private void checkValidPomId (List<String> messages)
  {
    try {
      if (Integer.valueOf(get(Type.pomId).getValue().trim()) <= 0) {
        messages.add("The pomId needs to be > 0");
      }
    }
    catch (Exception ignored) {
      messages.add("The pomId needs to be > 0");
    }
  }

  private void checkValidMultiplier (List<String> messages)
  {
    try {
      if (Integer.valueOf(get(Type.multiplier).getValue().trim()) <= 0) {
        messages.add("The multiplier needs to be > 0");
      }
      else {
        if (Integer.valueOf(get(Type.multiplier).getValue().trim()) > 10) {
          messages.add("The multiplier needs to be <= 10");
        }
      }
    }
    catch (Exception ignored) {
      messages.add("The multiplier needs to be > 0");
    }
  }

  private void checkValidBrokers (List<String> messages, String variableName)
  {
    if (Type.brokers.equals(variableName)) {
      return;
    }

    if (get(Type.brokers) == null) {
      messages.add("Brokers need to be defined");
    }
    else {
      try {
        String[] strArray = get(Type.brokers).getValue().split(",");
        for (String aStrArray : strArray) {
          if (Integer.valueOf(aStrArray) <= 0) {
            messages.add("BrokerIds need to be > 0");
          }
        }
        if (strArray.length == 0) {
          messages.add("Brokers need to be defined");
        }
      }
      catch (Exception ignored) {
        messages.add("Brokers definition not correct\nneeds to be ints separated by ','");
      }
    }
  }

  private void checkValidLocation (List<String> messages, String variableName)
  {
    if (Type.location.equals(variableName)) {
      return;
    }

    try {
      List<String> locations = Location.getLocationNames();
      if (!locations.contains(get(Type.location).getValue().trim())) {
        messages.add("The location isn't valid");
      }
    }
    catch (Exception ignored) {
      messages.add("Location needs to be defined");
    }
  }

  //<editor-fold desc="Proxy methods for the map">
  public Parameter get (String name)
  {
    return map.get(name);
  }

  public void put (String name, Parameter parameter)
  {
    map.put(name, parameter);
  }

  public Set<String> keySet ()
  {
    return map.keySet();
  }

  public Collection<Parameter> values ()
  {
    return map.values();
  }

  public Set<Map.Entry<String, Parameter>> entrySet ()
  {
    return map.entrySet();
  }

  public Parameter remove (String name)
  {
    return map.remove(name);
  }

  @Override
  public String toString ()
  {
    return map.toString();
  }
  //</editor-fold>

  //<editor-fold desc="Methods for Variable">
  public static void validateVariable (List<String> messages,
                                       String name, String value, int pomId)
  {
    if (name.isEmpty() || value.isEmpty()) {
      messages.add("No variable given, there will only be 1 experiment!");
      return;
    }

    Type variable = Type.get(pomId, name);
    if (Type.getVariableBaseTypes().contains(name)) {
      variable = Type.getBaseType(name);
    }

    // Check if variableName is allowed
    if (variable == null) {
      messages.add("Variable name '" + name + "' is not allowed!");
      return;
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
        else if (variable.clazz == Boolean.class) {
          Boolean.getBoolean(v);
        }
        // No need to check for String
      }
      catch (Exception ignored) {
        messages.add("Variable format isn't correct");
        break;
      }
    }
  }

  public static String parseMinMaxStep (String name, String minString,
                                        String maxString, String stepString,
                                        int pomId)
  {
    Type type = Type.get(pomId, name.trim());
    if (type == null) {
      return "";
    }
    else if (type.clazz == Integer.class) {
      int min = Integer.parseInt(minString);
      int max = Integer.parseInt(maxString);
      int step = Integer.parseInt(stepString);
      return String.join(",", getIntValues(min, max, step));
    }
    else if (type.clazz == Double.class) {
      double min = Double.parseDouble(minString);
      double max = Double.parseDouble(maxString);
      double step = Double.parseDouble(stepString);

      String[] parts = step < Math.min(min, max) ?
          stepString.split("\\.") : minString.split("\\.");
      int digits = parts.length > 1 ? parts[1].length() : 0;

      return String.join(",", getDoubleValues(min, max, step, digits));
    }

    return "";
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

