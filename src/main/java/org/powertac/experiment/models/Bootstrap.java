package org.powertac.experiment.models;

import org.powertac.experiment.services.Properties;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class Bootstrap
{
  private static Properties properties = Properties.getProperties();

  public static List<String> getBootstraps ()
  {
    String bootsLocation = properties.getProperty("bootLocation");
    File f = new File(bootsLocation);

    return Arrays.asList(f.list());
  }
}