package org.powertac.experiment.models;

import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Pom;
import org.powertac.experiment.services.Properties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class Type
{
  public String name;
  public Class clazz;
  public String preset;
  public String description;
  // TODO Check if needed
  public boolean exclusive = true;

  private Type (Object... attributes)
  {
    this.name = (String) attributes[0];
    this.clazz = (Class) attributes[1];
    if (attributes.length > 2 && attributes[2] != null) {
      this.preset = attributes[2].toString();
    }
    if (attributes.length > 3 && attributes[3] != null) {
      this.description = attributes[3].toString();
    }
    if (attributes.length > 4) {
      this.exclusive = (Boolean) attributes[4];
    }
  }

  public String getDefault ()
  {
    if (name.equals(brokers) || name.equals(pomId)) {
      return preset.split(" ")[0];
    }

    if (name.equals(bootstrapId) ||
        name.equals(seedId) ||
        name.equals(location)) {
      String result = preset.split(",")[0];

      // Needed for boot files
      result = result.replace("boot.", "").replace(".xml", "");

      // Needed for seed files
      result = result.replace("seed.", "").replace(".state", "");

      return result;
    }

    if (name.equals(simStartDate)) {
      return preset.split(" - ")[0];
    }

    if (name.equals(multiplier)) {
      return "2";
    }

    if (name.equals("server.weatherService.serverUrl")) {
      return preset;
    }

    return "";
  }

  public String[] getStringArray () {
    return new String[]{name, preset, getDefault(), description};
  }

  //////////////////////////////////////////////////////////////////////////////
  // Static stuff
  //////////////////////////////////////////////////////////////////////////////
  private static String scriptName = "dump_configs.sh";
  private static String typesName = "types.txt"; // Defined in script

  public static String brokers = "brokers";
  public static String pomId = "pomId";
  public static String bootstrapId = "bootstrapId";
  public static String seedId = "seedId";
  public static String location = "location";
  public static String simStartDate = "simStartDate";
  public static String createTime = "createTime";
  public static String startTime = "startTime";
  public static String multiplier = "multiplier";
  public static String gameLength = "gameLength";

  private static Map<String, Type> baseTypes = null;
  private static Map<Integer, Map<String, Type>> pomTypesMap = null;

  public static Type get (int pomId, String typeString)
  {
    Map<String, Type> pomMap = pomTypesMap.get(pomId);
    if (pomMap != null) {
      return pomMap.get(typeString);
    }
    return null;
  }

  public static Type pomId ()
  {
    return baseTypes.get(pomId);
  }

  public static Type gameLength ()
  {
    return baseTypes.get(gameLength);
  }

  public static Type createTime ()
  {
    return baseTypes.get(createTime);
  }

  public static Type startTime ()
  {
    return baseTypes.get(startTime);
  }

  public static Type location ()
  {
    return baseTypes.get(location);
  }

  public static Type simStartDate ()
  {
    return baseTypes.get(simStartDate);
  }

  public static Type bootstrapId ()
  {
    return baseTypes.get(bootstrapId);
  }

  public static Type seedId ()
  {
    return baseTypes.get(seedId);
  }

  public static Type brokers ()
  {
    return baseTypes.get(brokers);
  }

  public static Type multiplier ()
  {
    return baseTypes.get(multiplier);
  }

  private static void loadAll ()
  {
    if (baseTypes != null) {
      return;
    }

    loadStatic();
    loadGlobal();
    loadServerTypes();
  }

  private static void loadStatic ()
  {
    baseTypes = new HashMap<>();

    baseTypes.put(brokers, new Type(brokers, Integer.class, null,
        "Comma separated list of broker ids", false));
    baseTypes.put(pomId, new Type(pomId, Integer.class));
    baseTypes.put(bootstrapId, new Type(bootstrapId, Integer.class));
    baseTypes.put(seedId, new Type(seedId, Integer.class, null,
        "If not set, no seed file will be used"));
    baseTypes.put(location, new Type(location, String.class));
    baseTypes.put(simStartDate, new Type(simStartDate, String.class, null,
        "If not set, a random value will be used for all games in the set"));
    baseTypes.put(createTime, new Type(createTime, String.class));
    baseTypes.put(multiplier, new Type(multiplier, Integer.class, null,
        "Games per experiment"));
    baseTypes.put(gameLength, new Type(gameLength, Integer.class, null,
        "If not set, this will be randomized"));
    baseTypes.put(startTime, new Type(startTime, String.class));
  }

  private static void loadGlobal ()
  {
    List<Broker> brokerList = Broker.getBrokerList();
    if (brokerList.size() > 0) {
      baseTypes.get(brokers).preset =
          brokerList.stream().map(Broker::toString)
              .collect(Collectors.joining(", "));
    }

    List<Pom> pomList = Pom.getPomList();
    if (pomList.size() > 0) {
      baseTypes.get(pomId).preset =
          pomList.toString().replace("[", "").replace("]", "");
    }

    List<String> bootList = Bootstrap.getBootstraps();
    if (bootList.size() > 0) {
      baseTypes.get(bootstrapId).preset = bootList.stream()
          .map(p -> p.replace("bootstrap-", "").replace(".xml", ""))
          .collect(Collectors.joining(", "));
    }

    List<String> seedList = Seed.getSeeds();
    if (seedList.size() > 0) {
      baseTypes.get(seedId).preset = seedList.stream()
          .map(p -> p.replace("powertac-sim-", "").replace(".state", ""))
          .collect(Collectors.joining(", "));
    }

    List<Location> locList = Location.getLocationList();
    if (locList.size() > 0) {
      baseTypes.get(location).preset = locList.stream()
          .map(Location::getLocation)
          .collect(Collectors.joining(", "));

      baseTypes.get(simStartDate).preset = locList.stream()
          .map(Location::getRange)
          .collect(Collectors.joining(", "));
    }
  }

  private static void loadServerTypes ()
  {
    pomTypesMap = new HashMap<>();

    for (Pom pom : Pom.getPomList()) {
      int pomId = pom.getPomId();

      try {
        Map<String, Type> pomTypes = loadPomTypes(pom);
        pomTypesMap.put(pomId, pomTypes);
      }
      catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private static Map<String, Type> loadPomTypes (Pom pom)
      throws IOException, ClassNotFoundException
  {
    // Copy files to (newly created) temp dir
    Path tempDir = Files.createTempDirectory("tmp");
    copyFiles(tempDir, pom);

    // Run config dump
    dumpConfigs(tempDir);

    // Parse output
    Map<String, Type> pomTypes = parseConfigs(tempDir);

    // Remove temp dir
    tempDir.toFile().deleteOnExit();

    return pomTypes;
  }

  private static void copyFiles (Path tempDir, Pom pom) throws IOException
  {
    String pomLocation = Properties.getProperties().getProperty("pomLocation");

    // Copy (and rename) pom to temp dir
    Path targetPath = tempDir.resolve("pom.xml");
    Path sourcePath = Paths.get(pomLocation, pom.pomFileName());
    Files.copy(sourcePath, targetPath);

    // Copy script to temp dir
    targetPath = tempDir.resolve(scriptName);
    sourcePath = Paths.get(pomLocation, scriptName);
    Files.copy(sourcePath, targetPath);
  }

  private static void dumpConfigs (Path tempDir) throws IOException
  {
    Runtime rt = Runtime.getRuntime();
    String[] commands = {"./" + scriptName};
    Process proc = rt.exec(commands, null, tempDir.toFile());

    BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(proc.getInputStream()));
    BufferedReader stdError = new BufferedReader(new
        InputStreamReader(proc.getErrorStream()));

    // Wait until output is ready
    while (stdInput.readLine() != null || stdError.readLine() != null) {

    }
  }

  private static Map<String, Type> parseConfigs (Path tempDir)
      throws IOException, ClassNotFoundException
  {
    Path path = tempDir.resolve(typesName);
    FileReader fileReader = new FileReader(path.toFile());
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    List<String> lines = new ArrayList<>();
    String tmp;
    while ((tmp = bufferedReader.readLine()) != null) {
      lines.add(tmp);
    }
    fileReader.close();

    // Don't allow the user to set these, ES will handle them
    List<String> disallowed = Arrays.asList(
        "server.bootstrapDataFile",
        "server.competitionControlService.loginTimeout",
        "server.jmsManagementService.jmsBrokerUrl");

    Map<String, Type> pomTypes = new HashMap<>();
    String[] annotations = new String[3];
    int count = 0;
    for (String line : lines) {
      if (!line.startsWith("# ")) {
        String name = line.split("=")[0].trim();
        String preset = line.split("=")[1].trim();
        // Get the class from the annotation or infer from preset
        Class clazz = annotations[1] != null ?
            Class.forName(getClassName(annotations[1])) : findClass(preset);
        // Get the description if present
        String description = annotations[0] != null ?
            annotations[0].split(":")[1].trim() : null;

        if (!disallowed.contains(name)) {
          pomTypes.put(name, new Type(name, clazz, preset, description));
        }
        annotations = new String[3];
        count = 0;
      }
      else {
        annotations[count++] = line;
      }
    }

    return pomTypes;
  }

  private static String getClassName (String classLine)
  {
    String typeName = classLine.split(":")[1].trim();
    if (typeName.equals("List")) {
      return "java.lang.String";
    }
    else {
      return "java.lang." + typeName;
    }
  }

  private static Class findClass (String value)
  {
    try {
      Integer.valueOf(value);
      return Integer.class;
    }
    catch (NumberFormatException ignored) {
      try {
        Double.valueOf(value);
        return Double.class;
      }
      catch (NumberFormatException ignored2) {
        return String.class;
      }
    }
  }

  public static Set<Type> getTypes (int pomId)
  {
    Map<String, Type> pomTypes = pomTypesMap.get(pomId);

    if (pomTypes == null) {
      return new HashSet<>();
    }

    return new HashSet<>(pomTypes.values());
  }

  public static Set<Type> getBaseTypes ()
  {
    return new HashSet<>(baseTypes.values());
  }

  public static List<Type> getGameTypes ()
  {
    return Arrays.asList(Type.pomId(), Type.bootstrapId(), Type.location(),
        Type.simStartDate(), Type.gameLength(), Type.seedId());
  }

  static {
    loadAll();
  }
}
