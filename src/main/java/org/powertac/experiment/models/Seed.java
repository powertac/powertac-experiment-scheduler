package org.powertac.experiment.models;

import org.powertac.experiment.services.Properties;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class Seed
{
  private static Properties properties = Properties.getProperties();

  public static List<String> getSeeds ()
  {
    String seedsLocation = properties.getProperty("seedLocation");
    File f = new File(seedsLocation);

    return Arrays.asList(f.list());
  }
}