package org.powertac.experiment.models;

import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.ExperimentSet;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Pom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "parameters")
public class Parameter
{
  private int parameterId;
  private ExperimentSet experimentSet;
  private Experiment experiment;
  private Game game;
  private Type type;
  private String value;

  public Parameter ()
  {

  }

  public Parameter (Object owner, Type type, Object value)
  {
    setOwner(owner);
    this.type = type;
    this.value = String.valueOf(value);
  }

  public void setOwner (Object owner)
  {
    if (owner instanceof ExperimentSet) {
      this.experimentSet = (ExperimentSet) owner;
    }
    else if (owner instanceof Experiment) {
      this.experiment = (Experiment) owner;
    }
    else if (owner instanceof Game) {
      this.game = (Game) owner;
    }
  }

  @Transient
  public String typeName ()
  {
    return type.toString();
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
  @JoinColumn(name = "experimentSetId")
  public ExperimentSet getExperimentSet ()
  {
    return experimentSet;
  }

  public void setExperimentSet (ExperimentSet experimentSet)
  {
    this.experimentSet = experimentSet;
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
  @Enumerated(EnumType.STRING)
  public Type getType ()
  {
    return type;
  }

  public void setType (Type type)
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
  public static List<String> validateExperimentSetMap (ParamMap paramMap)
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
      } else {
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

  public static String getParamsString (ParamMap map)
  {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Type, Parameter> entry : map.entrySet()) {
      sb.append(entry.getKey().toString()).append("=");
      sb.append(entry.getValue().getValue()).append("\n");
    }
    return sb.toString();
  }

  public static String getDefaultString ()
  {
    StringBuilder sb = new StringBuilder();
    Type[] types = {Type.brokers, Type.pomId, Type.bootstrapId, Type.seedId,
        Type.location, Type.simStartDate, Type.multiplier};

    for (Type type : types) {
      sb.append(type.toString()).append("=")
          .append(type.getDefault()).append("\n");
    }

    return sb.toString();
  }

  public static List<String[]> getAvailableParams ()
  {
    List<String[]> availableParams = new ArrayList<>();

    for (Type type : Type.getExperimentSetTypes()) {
      if (type != Type.createTime) {
        availableParams.add(new String[]{type.toString(),
            type.preset, type.getDefault(), type.description});
      }
    }

    return availableParams;
  }

  public static List<String[]> getAvailableParamsOrg ()
  {
    List<String[]> availableParams = new ArrayList<>();
    List<Broker> brokers = Broker.getBrokerList();
    List<Pom> poms = Pom.getPomList();
    List<String> bootstraps = Bootstrap.getBootstraps();
    List<Location> locations = Location.getLocationList();

    for (Type type : Type.getExperimentSetTypes()) {
      if (type == Type.brokers && brokers.size() > 0) {
        availableParams.add(new String[]{type.toString(),
            brokers.toString().replace("[", "").replace("]", ""),
            brokers.get(0).toString().split(" ")[0],
            type.description});
      }
      else if (type == Type.pomId && poms.size() > 0) {
        availableParams.add(new String[]{type.toString(),
            poms.toString().replace("[", "").replace("]", ""),
            poms.get(0).toString().split(" ")[0],
            type.description});
      }
      else if (type == Type.bootstrapId) {
        availableParams.add(new String[]{type.toString(),
            bootstraps.stream().map(
                p -> p.replace("bootstrap-", "").replace(".xml", ""))
                .collect(Collectors.joining(", ")),
            bootstraps.get(0).replace("bootstrap-", "").replace(".xml", ""),
            type.description});
      }
      else if (type == Type.location) {
        availableParams.add(new String[]{type.toString(),
            locations.stream().map(Location::getLocation)
                .collect(Collectors.joining("<br/>")),
            locations.get(0).getLocation(),
            type.description});
      }
      else if (type == Type.simStartDate) {
        availableParams.add(new String[]{type.toString(),
            locations.stream().map(Location::getRange)
                .collect(Collectors.joining("<br/>")),
            locations.get(0).getRange(),
            type.description});
      }
      else if (type == Type.createTime) {
      }
      else {
        availableParams.add(
            new String[]{type.toString(), "", type.preset, type.description});
      }
    }

    return availableParams;
  }
  //</editor-fold>
}
