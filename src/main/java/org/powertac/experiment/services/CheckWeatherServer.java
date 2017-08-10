package org.powertac.experiment.services;

import org.apache.log4j.Logger;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.models.Type;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@ManagedBean
@Service("checkWeatherServer")
public class CheckWeatherServer implements InitializingBean
{
  private static Logger log = Utils.getLogger();
  private static String status = "";
  private final Properties properties;
  private Timer weatherServerCheckerTimer;
  private boolean mailed;

  @Autowired
  public CheckWeatherServer (Properties properties)
  {
    super();
    this.properties = properties;
  }

  public void afterPropertiesSet () throws Exception
  {
    Executors.newScheduledThreadPool(1)
        .schedule(this::lazyStart, 10, TimeUnit.SECONDS);
  }

  private void lazyStart ()
  {
    loadExtraLocations();

    // Check the status of the weather server every 15 minutes
    TimerTask weatherServerChecker = new TimerTask()
    {
      @Override
      public void run ()
      {
        ping();
      }
    };

    weatherServerCheckerTimer = new Timer();
    weatherServerCheckerTimer.schedule(weatherServerChecker, new Date(), 900000);
  }

  private void ping ()
  {
    log.info("Checking WeatherService");
    InputStream is = null;
    try {
      URL url = new URL(getWeatherServerLocation());
      URLConnection conn = url.openConnection();
      is = conn.getInputStream();

      int status = ((HttpURLConnection) conn).getResponseCode();
      if (status == 200) {
        setStatus("Server Alive and Well");
        log.info("Server Alive and Well");
        mailed = true;
      }
      else {
        setStatus("Server is Down");
        log.info("Server is Down");

        if (!mailed) {
          String msg = "It seems the WeatherServer is down";
          Utils.sendMail("WeatherServer is Down", msg,
              properties.getProperty("scheduler.mailRecipient"));
          mailed = true;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      setStatus("Server Timeout or Network Error");
      log.debug("Server Timeout or Network Error");

      if (!mailed) {
        String msg = "Server Timeour or Network Error during Weather Server ping";
        Utils.sendMail("WeatherServer Timeout or Network Error", msg,
            properties.getProperty("scheduler.mailRecipient"));
        mailed = true;
      }
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void loadExtraLocations ()
  {
    // Check the available weather locations at startup
    try {
      String url = String.format("%s?type=showLocations",
          getWeatherServerLocation());

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder docB = dbf.newDocumentBuilder();
      Document doc = docB.parse(new URL(url).openStream());
      NodeList nodeList = doc.getElementsByTagName("location");

      List<Location> availableLocations = new ArrayList<>();
      for (int i = 0; i < nodeList.getLength(); i++) {
        try {
          Element element = (Element) nodeList.item(i);
          Location location = new Location();
          location.setLocation(element.getAttribute("name"));
          location.setDateFrom(
              Utils.stringToDateMedium(element.getAttribute("minDate")));
          location.setDateTo(
              Utils.stringToDateMedium(element.getAttribute("maxDate")));
          availableLocations.add(location);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }

      MemStore.setAvailableLocations(availableLocations);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PreDestroy
  private void cleanUp () throws Exception
  {
    if (weatherServerCheckerTimer != null) {
      weatherServerCheckerTimer.cancel();
      weatherServerCheckerTimer.purge();
      weatherServerCheckerTimer = null;
      log.info("Stopping weatherServerCheckerTimer ...");
    }
    else {
      log.warn("weatherServerCheckerTimer Already Stopped");
    }
  }

  //<editor-fold desc="Setters and Getters">
  public String getWeatherServerLocation ()
  {
    return Type.server_weatherService_serverUrl.preset;
  }

  public String getStatus ()
  {
    return status;
  }

  public void setStatus (String newStatus)
  {
    status = newStatus;
  }
  //</editor-fold>
}
