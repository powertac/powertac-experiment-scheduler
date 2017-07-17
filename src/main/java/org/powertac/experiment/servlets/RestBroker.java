package org.powertac.experiment.servlets;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Agent;
import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.constants.Constants.Rest;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.AgentState;
import org.powertac.experiment.states.GameState;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.powertac.experiment.services.Utils.writeToFile;


@WebServlet(description = "REST API for brokers and agents",
    urlPatterns = {"/brokerLogin.jsp"})
public class RestBroker extends HttpServlet
{
  private static Logger log = Utils.getLogger();

  private static String responseType = "text/plain; charset=UTF-8";

  private static String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<message>";
  private static String tail = "</message>";
  private static String retryResponseXml = head + "<retry>%d</retry>" + tail;
  private static String loginResponseXml = head + "<login><jmsUrl>%s</jmsUrl><queueName>%s</queueName><serverQueue>%s</serverQueue></login>" + tail;
  private static String doneResponseXml = head + "<done></done>" + tail;
  private static String retryResponseJson = "{\n \"retry\":%d\n}";
  private static String loginResponseJson = "{\n \"login\":%d\n \"jmsUrl\":%s\n \"queueName\":%s\n \"serverQueue\":%s\n}";
  private static String doneResponseJson = "{\n \"done\":\"true\"\n}";

  private Properties properties = Properties.getProperties();

  public RestBroker ()
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
      if (actionString == null) {
        return parseBrokerLogin(request);
      }
      else if (actionString.equalsIgnoreCase(Rest.REQ_PARAM_AGENT_STATUS)) {
        return handleStatusAgent(request);
      }
    }
    catch (Exception ignored) {
    }
    return "error 2";
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
      String logLoc = properties.getProperty("logLocation");
      String pathString = logLoc + fileName;

      log.info("Received a file " + fileName);
      writeToFile(request, pathString);
    }
    catch (Exception e) {
      return "error";
    }
    return "success";
  }

  private String parseBrokerLogin (HttpServletRequest request)
  {
    String responseType = request.getParameter(Rest.REQ_PARAM_TYPE);
    String brokerAuth = request.getParameter(Rest.REQ_PARAM_AUTH_TOKEN);
    String joinName = request.getParameter(Rest.REQ_PARAM_JOIN);
    joinName = joinName.replace("game_", "");

    boolean isXml = responseType.equalsIgnoreCase("xml");
    String retryResponse = isXml ? retryResponseXml : retryResponseJson;
    String loginResponse = isXml ? loginResponseXml : loginResponseJson;
    String doneResponse = isXml ? doneResponseXml : doneResponseJson;

    log.info(String.format("Broker %s login request : %s", brokerAuth, joinName));

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.createQuery(Constants.HQL.GET_BROKER_BY_BROKERAUTH);
      query.setString("brokerAuth", brokerAuth);
      Broker broker = (Broker) query.uniqueResult();

      if (broker == null) {
        log.info("Broker doesn't exists : " + brokerAuth);
        return doneResponse;
      }
      log.debug("Broker id is : " + broker.getBrokerId());

      int gameId = Integer.parseInt(joinName);
      Game game = (Game) session.get(Game.class, gameId);
      if (game != null && game.getState().equals(GameState.game_ready)) {
        Agent agent = broker.getAgentMap().get(gameId);
        String readyString = getReadyString(agent, game, loginResponse);
        if (readyString != null) {
          session.update(agent);
          return readyString;
        }
      }

      log.debug("No games ready to start with id : " + joinName);
      MemStore.addBrokerCheckin(broker.getBrokerId());
      return String.format(retryResponse, 60);
    }
    catch (NumberFormatException ignored) {
      log.debug("Error parsing gameId, sending retry response");
      return String.format(retryResponse, 60);
    }
    catch (Exception e) {
      e.printStackTrace();
      log.error("Error, sending retry response");
      return String.format(retryResponse, 60);
    }
    finally {
      transaction.commit();
      session.close();
    }
  }

  @SuppressWarnings("unchecked")
  private String getReadyString (Agent agent, Game game, String loginResponse)
  {
    // Check if an other agent already checked in
    if (agent == null || !agent.getState().isPending()) {
      return null;
    }

    // Wait 10 seconds, game is set ready before it actually starts
    Utils.secondsSleep(10);

    log.debug("Game " + game.getGameId() + " is ready");

    agent.setState(AgentState.in_progress);

    log.info(String.format("Sending login to broker %s : %s, %s, %s",
        agent.getBroker().getBrokerName(), game.getMachine().getJmsUrl(),
        agent.getBrokerQueue(), game.getServerQueue()));
    return String.format(loginResponse, game.getMachine().getJmsUrl(),
        agent.getBrokerQueue(), game.getServerQueue());
  }

  private String handleStatusAgent (HttpServletRequest request)
  {
    String statusString = request.getParameter(Rest.REQ_PARAM_STATUS);
    int gameId = Integer.parseInt(request.getParameter(Rest.REQ_PARAM_GAMEID));
    int brokerId =
        Integer.parseInt(request.getParameter(Rest.REQ_PARAM_BROKERID));

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Game game = Game.getGame(session, gameId);
      if (game == null) {
        log.warn(String.format("Trying to set status %s on non-existing "
            + "game : %s", statusString, gameId));
        return "error";
      }

      Agent agent = game.getAgentMap().get(brokerId);
      if (agent == null) {
        log.warn(String.format("Trying to set status %s on non-existing "
            + "broker : %s", statusString, brokerId));
        return "error";
      }

      Machine.delayedMachineUpdate(agent.getMachine(), 10);
      agent.setMachine(null);
      agent.setState(AgentState.valueOf(statusString));
      transaction.commit();

      return "success";
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
}
