package org.powertac.experiment.servlets;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.schedulers.GameHandler;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.powertac.experiment.constants.Constants.Rest;


@WebServlet(description = "REST API for game servers",
    urlPatterns = {"/serverInterface.jsp"})
public class RestServer extends HttpServlet
{
  private static Logger log = Utils.getLogger();

  private static String responseType = "text/plain; charset=UTF-8";

  private Properties properties = Properties.getProperties();

  public RestServer ()
  {
    super();
  }

  synchronized protected void doGet (HttpServletRequest request,
                                     HttpServletResponse response)
      throws IOException
  {
    String result = handleGET(request);
    writeResult(response, result);
  }

  synchronized protected void doPut (HttpServletRequest request,
                                     HttpServletResponse response)
      throws IOException
  {
    String result = handlePUT(request);
    writeResult(response, result);
  }

  synchronized protected void doPost (HttpServletRequest request,
                                      HttpServletResponse response)
      throws IOException
  {
    String result = handlePOST(request);
    writeResult(response, result);
  }

  private void writeResult (HttpServletResponse response, String result)
      throws IOException
  {
    response.setContentType(responseType);
    response.setContentLength(result.length());

    PrintWriter out = response.getWriter();
    out.print(result);
    out.flush();
    out.close();
  }

  /**
   * Handle 'GET' to serverInterface.jsp, server status / heartbeats
   */
  private String handleGET (HttpServletRequest request)
  {
    if (!MemStore.checkMachineAllowed(request.getRemoteAddr())) {
      return "error";
    }

    try {
      String actionString = request.getParameter(Rest.REQ_PARAM_ACTION);
      if (actionString.equalsIgnoreCase(Rest.REQ_PARAM_STATUS)) {
        return handleStatus(request);
      }
      else if (actionString.equalsIgnoreCase(Rest.REQ_PARAM_BOOT)) {
        Game game = Game.getGameById(getGameId(request));
        String bootId = game.getParamMap().getValue(Type.bootstrapId);
        return serveBoot(Integer.parseInt(bootId));
      }
      else if (actionString.equalsIgnoreCase(Rest.REQ_PARAM_SEED)) {
        Game game = Game.getGameById(getGameId(request));
        String seedId = game.getParamMap().getValue(Type.seedId);
        return serveSeed(Integer.parseInt(seedId));
      }
      else if (actionString.equalsIgnoreCase(Rest.REQ_PARAM_HEARTBEAT)) {
        return handleHeartBeat(request);
      }
    }
    catch (Exception ignored) {
    }
    return "error";
  }

  /**
   * Handle 'PUT' to serverInterface.jsp, either boot.xml or (Boot|Sim) log
   */
  private String handlePUT (HttpServletRequest request)
  {
    if (!MemStore.checkMachineAllowed(request.getRemoteAddr())) {
      return "error";
    }

    try {
      String fileName = request.getParameter(Rest.REQ_PARAM_FILENAME);
      log.info("Received a file " + fileName);

      String logLoc = fileName.endsWith("boot.xml")
          ? properties.getProperty("bootLocation")
          : properties.getProperty("logLocation");
      String pathString = logLoc + fileName;

      // Write to file
      InputStream is = request.getInputStream();
      FileOutputStream fos = new FileOutputStream(pathString);
      byte buf[] = new byte[1024];
      int letti;
      while ((letti = is.read(buf)) > 0) {
        fos.write(buf, 0, letti);
      }
      is.close();
      fos.close();

      // Create softlinks to named versions
      String gameName = request.getParameter(Rest.REQ_PARAM_GAMENAME);
      createSoftLinks (fileName, logLoc, gameName);
    }
    catch (Exception e) {
      return "error";
    }
    return "success";
  }

  /**
   * Handle 'POST' to serverInterface.jsp, this is an end-of-game message
   */
  private String handlePOST (HttpServletRequest request)
  {
    if (!MemStore.checkMachineAllowed(request.getRemoteAddr())) {
      return "error";
    }

    try {
      String actionString = request.getParameter(Rest.REQ_PARAM_ACTION);
      if (!actionString.equalsIgnoreCase(Rest.REQ_PARAM_GAMERESULTS)) {
        log.debug("The message didn't have the right action-string!");
        return "error";
      }

      int gameId = getGameId(request);
      if (!(gameId > 0)) {
        log.debug("The message didn't have a gameId!");
        return "error";
      }

      Session session = HibernateUtil.getSession();
      Transaction transaction = session.beginTransaction();
      try {
        Game game = (Game) session.get(Game.class, gameId);
        String standings = request.getParameter(Rest.REQ_PARAM_MESSAGE);
        return new GameHandler(game).handleStandings(session, standings, true);
      }
      catch (Exception e) {
        transaction.rollback();
        e.printStackTrace();
        return "error";
      }
      finally {
        session.close();
      }
    }
    catch (Exception e) {
      log.error("Something went wrong with receiving the POST message!");
      log.error(e.getMessage());
      return "error";
    }
  }

  private String serveBoot (int bootId)
  {
    StringBuilder result = new StringBuilder();

    try {
      // Determine boot-file location
      String bootLocation = properties.getProperty("bootLocation") +
          "boot." + bootId + ".xml";

      // Read the file
      FileInputStream fstream = new FileInputStream(bootLocation);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      while ((strLine = br.readLine()) != null) {
        result.append(strLine).append("\n");
      }

      // Close the streams
      fstream.close();
      in.close();
      br.close();

      return result.toString();
    }
    catch (Exception e) {
      log.error(e.getMessage());
      return "error";
    }
  }

  private String serveSeed (int seedId)
  {
    StringBuilder result = new StringBuilder();

    try {
      // Determine seed-file location
      String bootLocation = properties.getProperty("seedLocation") +
          "seed." + seedId + ".state";

      // Read the file
      FileInputStream fstream = new FileInputStream(bootLocation);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      while ((strLine = br.readLine()) != null) {
        result.append(strLine).append("\n");
      }

      // Close the streams
      fstream.close();
      in.close();
      br.close();

      return result.toString();
    }
    catch (Exception e) {
      log.error(e.getMessage());
      return "error";
    }
  }

  private String handleStatus (HttpServletRequest request)
  {
    String statusString = request.getParameter(Rest.REQ_PARAM_STATUS);
    int gameId = getGameId(request);

    log.info(String.format("Received %s message from game: %s",
        statusString, gameId));

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Game game = Game.getGame(session, gameId);

      if (game == null) {
        log.warn(String.format("Trying to set status %s on non-existing "
            + "game : %s", statusString, gameId));
        return "error";
      }

      new GameHandler(game).handleStatus(session, statusString);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      return "error";
    }
    finally {
      session.close();
    }

    String gameLength = request.getParameter(Rest.REQ_PARAM_GAMELENGTH);
    if (gameLength != null && transaction.wasCommitted()) {
      log.info(String.format("Received gamelength %s for game %s",
          gameLength, gameId));
      MemStore.addGameLength(gameId, gameLength);
    }

    return "success";
  }

  private String handleHeartBeat (HttpServletRequest request)
  {
    int gameId;

    // Write heartbeat + elapsed time to the MemStore
    try {
      String message = request.getParameter(Rest.REQ_PARAM_MESSAGE);
      gameId = getGameId(request);

      if (!(gameId > 0)) {
        log.debug("The message didn't have a gameId!");
        return "error";
      }
      MemStore.addGameHeartbeat(gameId, message);

      long elapsedTime = Long.parseLong(
          request.getParameter(Rest.REQ_PARAM_ELAPSED_TIME));
      if (elapsedTime > 0) {
        MemStore.addElapsedTime(gameId, elapsedTime);
      }

      return "success";
    }
    catch (Exception e) {
      e.printStackTrace();
      return "error";
    }
  }

  private int getGameId (HttpServletRequest request)
  {
    int gameId;

    try {
      gameId = Integer.parseInt(request.getParameter(Rest.REQ_PARAM_GAMEID));
    }
    catch (Exception ignored) {
      String niceName = request.getParameter(Rest.REQ_PARAM_GAMEID);
      gameId = MemStore.getGameId(niceName);
    }

    return gameId;
  }

  private void createSoftLinks (String fileName, String logLoc, String gameName)
  {
    String linkName;
    if (fileName.endsWith("boot.xml")) {
      linkName = String.format("%s%s.boot.xml", logLoc, gameName);
    }
    else if (fileName.contains("boot")) {
      linkName = String.format("%s%s.boot.tar.gz", logLoc, gameName);
    }
    else {
      linkName = String.format("%s%s.sim.tar.gz", logLoc, gameName);
    }

    try {
      Path link = Paths.get(linkName);
      Path target = Paths.get(fileName);
      Files.createSymbolicLink(link, target);
    }
    catch (FileAlreadyExistsException faee) {
      // Ignored
    }
    catch (IOException | UnsupportedOperationException e) {
      e.printStackTrace();
    }
  }
}