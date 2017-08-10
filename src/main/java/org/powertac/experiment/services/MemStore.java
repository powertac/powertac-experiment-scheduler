package org.powertac.experiment.services;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Config;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component("memStore")
public class MemStore
{
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