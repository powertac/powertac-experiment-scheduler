package org.powertac.experiment.models;

import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "parameters")
public class Parameter
{
  private int parameterId;
  private Study study;
  private Experiment experiment;
  private Game game;
  private String type;
  private String value;

  public Parameter ()
  {

  }

  public Parameter (Object owner, String type, Object value)
  {
    setOwner(owner);
    this.type = type;
    this.value = String.valueOf(value);
  }

  private void setOwner (Object owner)
  {
    if (owner instanceof Study) {
      this.study = (Study) owner;
    }
    else if (owner instanceof Experiment) {
      this.experiment = (Experiment) owner;
    }
    else if (owner instanceof Game) {
      this.game = (Game) owner;
    }
  }

  //<editor-fold desc="Getters and Setters">
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "parameterId", unique = true, nullable = false)
  public int getParameterId ()
  {
    return parameterId;
  }

  public void setParameterId (int parameterId)
  {
    this.parameterId = parameterId;
  }

  @ManyToOne
  @JoinColumn(name = "studyId")
  public Study getStudy ()
  {
    return study;
  }

  public void setStudy (Study study)
  {
    this.study = study;
  }

  @ManyToOne
  @JoinColumn(name = "experimentId")
  public Experiment getExperiment ()
  {
    return experiment;
  }

  public void setExperiment (Experiment experiment)
  {
    this.experiment = experiment;
  }

  @ManyToOne
  @JoinColumn(name = "gameId")
  public Game getGame ()
  {
    return game;
  }

  public void setGame (Game game)
  {
    this.game = game;
  }

  @Column(name = "type", nullable = false)
  //@Enumerated(EnumType.STRING)
  public String getType ()
  {
    return type;
  }

  public void setType (String type)
  {
    this.type = type;
  }

  @Column(name = "value", nullable = false)
  public String getValue ()
  {
    return value;
  }

  public void setValue (String value)
  {
    this.value = value;
  }
  //</editor-fold>

  //<editor-fold desc="Static stuff">
  // TODO Move to ParamMap
  public static List<String> validateStudyMap (ParamMap paramMap)
  {
    List<String> messages = new ArrayList<>();

    try {
      if (Integer.valueOf(paramMap.get(Type.pomId).getValue().trim()) <= 0) {
        messages.add("The pomId needs to be > 0");
      }
    }
    catch (Exception ignored) {
      messages.add("The pomId needs to be > 0");
    }

    try {
      if (Integer.valueOf(paramMap.get(Type.multiplier).getValue().trim()) <= 0) {
        messages.add("The multiplier needs to be > 0");
      }
      else {
        if (Integer.valueOf(paramMap.get(Type.multiplier).getValue().trim()) > 10) {
          messages.add("The multiplier needs to be <= 10");
        }
      }
    }
    catch (Exception ignored) {
      messages.add("The multiplier needs to be > 0");
    }

    if (paramMap.get(Type.brokers) == null) {
      messages.add("Brokers need to be defined");
    }
    else {
      try {
        String[] strArray = paramMap.get(Type.brokers).getValue().split(",");
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

    // TODO Check simStartDate, bootstrapId, gameLength
    try {
      List<String> locations = Location.getLocationNames();
      if (!locations.contains(paramMap.get(Type.location).getValue().trim())) {
        messages.add("The location isn't valid");
      }
    }
    catch (Exception ignored) {
      messages.add("Location needs to be defined");
    }

    try {
      List<String> locations = Location.getLocationNames();
      if (!locations.contains(paramMap.get(Type.location).getValue().trim())) {
        messages.add("The location isn't valid");
      }
    }
    catch (Exception ignored) {
      messages.add("Location needs to be defined");
    }

    return messages;
  }

  public static List<ParamEntry> getDefaultList (int minLength)
  {
    List<ParamEntry> defaultList = new ArrayList<>();

    Type[] types = {Type.brokers(), Type.bootstrapId(),
        Type.seedId(), Type.location(), Type.simStartDate(), Type.multiplier()};

    for (Type type : types) {
      defaultList.add(new ParamEntry(type.name, type.getDefault()));
    }
    while (defaultList.size() < minLength) {
      defaultList.add(new ParamEntry("", ""));
    }

    return defaultList;
  }

  public static List<ParamEntry> getParamList (ParamMap map, int minLength)
  {
    List<ParamEntry> paramList = new ArrayList<>();

    for (Map.Entry<String, Parameter> entry : map.entrySet()) {
      if (entry.getKey().equals(Type.pomId)) {
        continue;
      }
      paramList.add(new ParamEntry(entry.getKey(), entry.getValue().value));
    }
    while (paramList.size() < minLength) {
      paramList.add(new ParamEntry("", ""));
    }

    return paramList;
  }

  public static List<String[]> getAvailableServerParams (int pomId)
  {
    List<String[]> availableParams = new ArrayList<>();

    for (Type type : Type.getTypes(pomId)) {
      if (type.name.equals(Type.createTime) ||
          type.name.equals(Type.startTime)) {
        continue;
      }

      availableParams.add(type.getStringArray());
    }

    availableParams.sort(Comparator.comparing(p -> p[0]));

    return availableParams;
  }

  public static List<String[]> getAvailableBaseParams ()
  {
    List<String[]> availableParams = new ArrayList<>();

    for (Type type : Type.getBaseTypes()) {
      if (type.name.equals(Type.createTime) ||
          type.name.equals(Type.startTime)) {
        continue;
      }

      availableParams.add(type.getStringArray());
    }

    availableParams.sort(Comparator.comparing(p -> p[0]));

    return availableParams;
  }
  //</editor-fold>
}
