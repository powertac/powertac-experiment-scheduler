package org.powertac.experiment.servlets;

import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Scheduler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;


@WebServlet(description = "REST API for web", urlPatterns = {"/Rest"})
public class RestWeb extends HttpServlet
{
  private static String responseType = "application/json";

  public RestWeb ()
  {
    super();
  }

  protected void doPost (HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    String type = request.getParameter("type");

    if (type.equals("brokerState")) {
      int brokerId = Integer.valueOf(request.getParameter("brokerId"));
      MemStore.setBrokerState(brokerId, !MemStore.getBrokerState(brokerId));
    }
  }

  protected void doGet (HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    String type = request.getParameter("type");

    String result = "{}";
    if (type.equals("brokers")) {
      result = parseBrokers();
    }
    else if (type.equals("games")) {
      result = parseGames();
    }
    else if (type.equals("scheduler")) {
      result = parseScheduler();
    }
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

  private String parseBrokers ()
  {
    String result = "{ ";

    for (Integer i : MemStore.getBrokerCheckins().keySet()) {
      if (MemStore.getBrokerCheckins().get(i) == null) {
        continue;
      }

      result += "\"" + i.toString() + "\": \"";

      Iterator<Long> iter = MemStore.getBrokerCheckins().get(i).iterator();
      while (iter.hasNext()) {
        Long checkin = iter.next();
        int stamp = (int) (System.currentTimeMillis() - checkin) / 1000;
        if (stamp > 900) {
          iter.remove();
          MemStore.removeBrokerCheckin(i, checkin);
        }
        else if (stamp < 60) {
          result += "<b>" + stamp + "</b> ";
        }
        else {
          result += stamp + " ";
        }
      }
      result += "\" , ";
    }

    if (result.length() > 2) {
      result = result.substring(0, result.length() - 2);
    }
    result += " }";

    return result;
  }

  private String parseGames ()
  {
    String result = "{ ";

    for (Integer i : MemStore.getGameHeartbeats().keySet()) {
      String[] messages = MemStore.getGameHeartbeats().get(i);
      if (messages == null) {
        continue;
      }

      result += "\"" + i.toString() + "\": \"";

      try {
        int stamp = (int)
            (System.currentTimeMillis() - Long.parseLong(messages[1])) / 1000;
        if (stamp > 900) {
          MemStore.removeGameInfo(i);
        }
        else {
          Integer gameLength = MemStore.getGameLengths().get(i);
          if (gameLength == null) {
            result += messages[0] + " (" + stamp + ")";
          }
          else {
            result += messages[0] + " / " + gameLength + " (" + stamp + ")";
          }

          Long tmp = MemStore.getElapsedTimes().get(i);
          if (tmp != null) {
            result += ";" + String.valueOf(tmp);
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      result += "\" , ";
    }

    if (result.length() > 2) {
      result = result.substring(0, result.length() - 2);
    }
    result += " }";

    return result;
  }

  private String parseScheduler ()
  {
    Scheduler scheduler = Scheduler.getScheduler();
    return "{ \"text\": \"Scheduler running "
        + scheduler.getLastSchedulerRun() + "\" }";
  }
}