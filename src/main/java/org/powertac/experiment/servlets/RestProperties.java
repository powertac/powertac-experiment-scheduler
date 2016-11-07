package org.powertac.experiment.servlets;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.constants.Constants.Prop;
import org.powertac.experiment.constants.Constants.Rest;
import org.powertac.experiment.models.ParamMap;
import org.powertac.experiment.models.Parameter;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Properties;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(description = "REST API to retrieve the properties",
    urlPatterns = {"/properties.jsp"})
public class RestProperties extends HttpServlet
{
  private static String responseType = "text/plain; charset=UTF-8";

  private Properties properties = Properties.getProperties();

  public RestProperties ()
  {
    super();
  }

  protected void doGet (HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    String result = request.getParameter(Rest.REQ_PARAM_BROKERID) != null
        ? parseBrokerProperties(request)
        : parseGameProperties(request);
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

  private String parseGameProperties (HttpServletRequest request)
  {
    // Allow slaves and admin users
    if (!MemStore.checkMachineAllowed(request.getRemoteAddr())) {
      return "error";
    }

    int gameId;
    try {
      String niceName = request.getParameter(Rest.REQ_PARAM_GAMEID);
      gameId = MemStore.getGameId(niceName);
    }
    catch (Exception ignored) {
      return "";
    }

    Game game;
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      game = Game.getGame(session, gameId);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      return "";
    }
    finally {
      session.close();
    }

    return getGamePropertiesString(game);
  }

  private String getGamePropertiesString (Game game)
  {
    StringBuilder result = new StringBuilder();

    // Properties from the game
    ParamMap gameMap = game.getParamMap();

    // Add the Global params
    result.append(Prop.weatherServerURL)
        .append(gameMap.getValue(Type.server_weatherService_serverUrl)).append("\n");
    result.append(Prop.weatherLocation)
        .append(gameMap.getValue(Type.location)).append("\n");
    Parameter locParam = gameMap.get(Type.location);
    if (locParam != null) {
      Location location = Location.getLocationByName(locParam.getValue());
      result.append(Prop.timezoneOffset).append(location.getTimezone()).append("\n");
    }
    result.append(Prop.startTime).append(gameMap.getValue(Type.startTime)).append("\n");

    result.append(Prop.jms);
    if (game.getMachine() != null) {
      result.append(game.getMachine().getJmsUrl()).append("\n");
    }
    else {
      result.append("tcp://localhost:61616").append("\n");
    }

    result.append(Prop.serverFirstTimeout).append(600000).append("\n");
    result.append(Prop.serverTimeout).append(120000).append("\n");

    // Properties from the experiment
    Parameter gameLength = gameMap.get(Type.gameLength);
    if (gameLength != null) {
      result.append(Constants.Prop.minTimeslot).append(gameLength.getValue()).append("\n");
      result.append(Prop.expectedTimeslot).append(gameLength.getValue()).append("\n");
    }

    // Add the Server params
    for (Parameter param : gameMap.values()) {
      Type type = param.getType();
      if (!type.toString().contains("_") ||
          type == Type.server_weatherService_serverUrl ||
          type == Type.common_competition_timezoneOffset) {
        continue;
      }

      result.append(type.toString().replace("_", ".")).append(" = ")
          .append(param.getValue()).append("\n");
    }

    return result.toString();
  }

  /**
   * Returns a broker properties file string
   *
   * @param params :
   * @return String representing a broker properties file
   */
  private String parseBrokerProperties (HttpServletRequest request)
  {
    // Allow slaves and admin users
    if (!MemStore.checkMachineAllowed(request.getRemoteAddr())) {
      return "error";
    }

    Broker broker;
    int gameId = -1;

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      int brokerId = Integer.parseInt(
          request.getParameter(Rest.REQ_PARAM_BROKERID));
      broker = (Broker) session.get(Broker.class, brokerId);
      gameId = Integer.parseInt(
          request.getParameter(Rest.REQ_PARAM_GAMEID));
    }
    catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    finally {
      transaction.commit();
      session.close();
    }

    return getBrokerPropertiesString(broker, gameId);
  }

  private String getBrokerPropertiesString (Broker broker, int gameId)
  {
    StringBuilder result = new StringBuilder();

    result.append(Constants.Prop.brokerUsername)
        .append(broker.getBrokerName()).append("\n");
    result.append(Prop.authToken).append(broker.getBrokerAuth())
        .append("\n");
    result.append(String.format(Prop.tourneyUrl,
        properties.getProperty("tourneyUrl"))).append("\n");

    result.append(Prop.tourneyName).append("game_").append(gameId).append("\n");

    return result.toString();
  }
}