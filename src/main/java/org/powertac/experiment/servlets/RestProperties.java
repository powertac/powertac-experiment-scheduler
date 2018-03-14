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
    // TODO Get from some list
    addParam(result, Prop.weatherServerURL,
        gameMap.getValue("server.weatherService.serverUrl"));
    addParam(result, Prop.weatherLocation, gameMap.getValue(Type.location));

    Parameter locParam = gameMap.get(Type.location);
    if (locParam != null) {
      Location location = Location.getLocationByName(locParam.getValue());
      addParam(result, Prop.timezoneOffset, location.getTimezone());
    }
    addParam(result, Prop.startTime, gameMap.getValue(Type.startTime));
    addParam(result, Prop.jms, "tcp://0.0.0.0:61616");
    addParam(result, Prop.serverFirstTimeout, 600000);
    addParam(result, Prop.serverTimeout, 120000);
    Parameter gameLength = gameMap.get(Type.gameLength);
    if (gameLength != null) {
      addParam(result, Prop.minTimeslot, gameLength.getValue());
      addParam(result, Prop.expectedTimeslot, gameLength.getValue());
    }

    // Properties from the experiment- and game paramMap
    addParamMap(result, game.getExperiment().getParamMap());
    addParamMap(result, gameMap);

    return result.toString();
  }

  /**
   * Returns a broker properties file string
   *
   * @return String representing a broker properties file
   */
  private String parseBrokerProperties (HttpServletRequest request)
  {
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

    addParam(result, Constants.Prop.brokerUsername, broker.getBrokerName());
    addParam(result, Prop.authToken, broker.getBrokerAuth());
    addParam(result, Prop.tourneyUrl, String.format(Prop.tourneyPath,
        properties.getProperty("tourneyUrl")));
    addParam(result, Prop.tourneyName, String.format("game_%d", gameId));

    return result.toString();
  }

  private void addParam (StringBuilder result, String name, Object value)
  {
    result.append(name);
    if (!name.endsWith(" = ")) {
      result.append(" = ");
    }
    result.append(value).append("\n");
  }

  private void addParamMap (StringBuilder result, ParamMap paramMap)
  {
    for (Parameter param : paramMap.values()) {
      String name = param.getType();

      // TODO Get from some list
      // TODO Check for dot?
      if (name.contains(".") &&
          !name.equals("server.weatherService.serverUrl") &&
          !name.equals("common.competition.timezoneOffset")) {
        addParam(result, name, param.getValue());
      }
    }
  }
}
