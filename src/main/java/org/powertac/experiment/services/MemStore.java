package org.powertac.experiment.services;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Config;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.beans.User;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component("memStore")
public class MemStore
{
  private static Logger log = Utils.getLogger();

  private static ConcurrentHashMap<String, List<String>> machineIPs;
  private static ConcurrentHashMap<String, String> localIPs;

  private static ConcurrentHashMap<Integer, List<Long>> brokerCheckins;
  private static ConcurrentHashMap<Integer, String[]> gameHeartbeats;
  private static ConcurrentHashMap<Integer, Integer> gameLengths;
  private static ConcurrentHashMap<Integer, Long> elapsedTimes;

  private static ConcurrentHashMap<String, Integer> gameIds;

  private static ConcurrentHashMap<Integer, Boolean> brokerState;
  private static List<Location> availableLocations;

  private static String indexContent;

  public MemStore ()
  {
    machineIPs = null;
    localIPs = null;

    brokerCheckins = new ConcurrentHashMap<>(50, 0.9f, 1);
    gameHeartbeats = new ConcurrentHashMap<>(20, 0.9f, 1);
    gameLengths = new ConcurrentHashMap<>(20, 0.9f, 1);
    elapsedTimes = new ConcurrentHashMap<>();

    brokerState = new ConcurrentHashMap<>(50, 0.9f, 1);
    availableLocations = new ArrayList<>();
  }

  public static int getGameId (String niceName)
  {
    Integer gameId = gameIds.get(niceName);
    if (gameId == null) {
      gameId = 0;
    }
    return gameId;
  }

  public static void getNameMapping (boolean force)
  {
    if (gameIds == null) {
      gameIds = new ConcurrentHashMap<>(200, 0.9f, 1);
    }
    else if (!force) {
      return;
    }

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      for (Object obj : session.createCriteria(Game.class).list()) {
        Game game = (Game) obj;
        gameIds.put(game.getGameName(), game.getGameId());
      }
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    finally {
      session.close();
    }
  }

  //<editor-fold desc="IP stuff">
  public static void getIpAddresses ()
  {
    machineIPs = new ConcurrentHashMap<>(20, 0.9f, 1);
    localIPs = new ConcurrentHashMap<>(20, 0.9f, 1);

    for (Machine m : Machine.getMachineList()) {
      try {
        String machineIP = InetAddress.getByName(m.getMachineUrl()).toString();
        if (machineIP.contains("/")) {
          machineIP = machineIP.split("/")[1];
        }

        List<String> machine =
            Arrays.asList(m.getMachineName(), m.getMachineId().toString());
        machineIPs.put(machineIP, machine);
      }
      catch (UnknownHostException ignored) {
      }
    }

    localIPs.put("127.0.0.1", "loopback");
    localIPs.put("0:0:0:0:0:0:0:1", "loopback_IPv6");
    try {
      for (Enumeration<NetworkInterface> en =
           NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
        NetworkInterface intf = en.nextElement();

        if (!intf.getName().startsWith("eth") &&
            !intf.getName().startsWith("vboxnet")) {
          continue;
        }

        for (Enumeration<InetAddress> enumIpAddr =
             intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
          String ip = enumIpAddr.nextElement().toString();
          if (ip.contains(":")) {
            continue;
          }
          if (ip.contains("/")) {
            ip = ip.split("/")[1];
          }
          localIPs.put(ip, intf.getName());
        }
      }
    }
    catch (SocketException e) {
      log.error(" (error retrieving network interface list)");
    }
  }

  public static void resetMachineIPs ()
  {
    machineIPs = null;
  }

  public static boolean checkMachineAllowed (String slaveAddress)
  {
    User user = User.getCurrentUser();
    if (user != null && user.isAdmin()) {
      return true;
    }

    if (machineIPs == null) {
      getIpAddresses();
    }

    assert localIPs != null;
    if (localIPs.containsKey(slaveAddress)) {
      //log.debug("Localhost is always allowed");
      return true;
    }

    assert machineIPs != null;
    if (machineIPs.containsKey(slaveAddress)) {
      //log.debug(slaveAddress + " is allowed");
      return true;
    }

    log.debug(slaveAddress + " is not allowed");
    return false;
  }
  //</editor-fold>

  //<editor-fold desc="Checkin stuff">
  public static ConcurrentHashMap<Integer, List<Long>> getBrokerCheckins ()
  {
    return brokerCheckins;
  }

  public synchronized static void addBrokerCheckin (int brokerId)
  {
    List<Long> dates = brokerCheckins.get(brokerId);
    if (dates == null) {
      dates = new ArrayList<>();
    }

    dates.add(System.currentTimeMillis());

    if (dates.size() > 4) {
      dates.remove(0);
    }

    brokerCheckins.put(brokerId, dates);
  }

  public static void removeBrokerCheckin (int brokerId, long checkin)
  {
    brokerCheckins.get(brokerId).remove(checkin);
  }

  public static ConcurrentHashMap<Integer, String[]> getGameHeartbeats ()
  {
    return gameHeartbeats;
  }

  public synchronized static void addGameHeartbeat (int gameId, String message)
  {
    gameHeartbeats.put(gameId,
        new String[]{message, System.currentTimeMillis() + ""});
  }

  public static ConcurrentHashMap<Integer, Long> getElapsedTimes ()
  {
    return elapsedTimes;
  }

  public synchronized static void addElapsedTime (int gameId, long elapsedtime)
  {
    elapsedTimes.put(gameId, elapsedtime);
  }

  public static ConcurrentHashMap<Integer, Integer> getGameLengths ()
  {
    return gameLengths;
  }

  public synchronized static void addGameLength (int gameId, String gameLength)
  {
    try {
      gameLengths.put(gameId, Integer.parseInt(gameLength) +
          Properties.getProperties().getPropertyInt("bootLength"));
    }
    catch (Exception ignored) {
    }
  }

  public synchronized static void removeGameInfo (int gameId)
  {
    gameHeartbeats.remove(gameId);
    gameLengths.remove(gameId);
    elapsedTimes.remove(gameId);
  }
  //</editor-fold>

  //<editor-fold desc="Interface stuff">
  public static boolean getBrokerState (int brokerId)
  {
    try {
      return MemStore.brokerState.get(brokerId);
    }
    catch (Exception ignored) {
      return true;
    }
  }

  public static void setBrokerState (int brokerId, boolean state)
  {
    brokerState.put(brokerId, state);
  }

  public static List<Location> getAvailableLocations ()
  {
    return availableLocations;
  }

  public static void setAvailableLocations (List<Location> availableLocations)
  {
    MemStore.availableLocations = availableLocations;
  }
  //</editor-fold>

  //<editor-fold desc="Content stuff">
  public static String getIndexContent ()
  {
    if (indexContent == null || indexContent.isEmpty()) {
      indexContent = Config.getIndexContent();
      if (indexContent == null) {
        return "Error connecting to DB";
      }
    }

    return indexContent;
  }

  public static boolean setIndexContent (String newContent)
  {
    indexContent = newContent;

    return Config.setIndexContent(newContent);
  }
  //</editor-fold>
}