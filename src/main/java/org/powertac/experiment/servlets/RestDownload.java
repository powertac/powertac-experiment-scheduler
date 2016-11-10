package org.powertac.experiment.servlets;

import org.apache.log4j.Logger;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import static org.powertac.experiment.constants.Constants.Rest;


/**
 * Servlet implementation class Downloader
 */
@WebServlet(description = "Access to download compressed logfiles",
    urlPatterns = {"/download"})
public class RestDownload extends HttpServlet
{
  private static Logger log = Utils.getLogger();

  Properties properties = Properties.getProperties();

  protected void doGet (HttpServletRequest request, HttpServletResponse response)
  {
    String downloadFile;
    String absolutePath;
    String gameId = request.getParameter("game");
    String bootId = request.getParameter("boot");
    String csvName = request.getParameter("csv");
    String pomId = request.getParameter(Rest.REQ_PARAM_POM_ID);
    String brokerId = request.getParameter(Rest.REQ_PARAM_BROKERID);

    if (brokerId != null && gameId != null) {
      absolutePath = properties.getProperty("logLocation");
      downloadFile = "game-" + gameId + "-broker-" + brokerId + ".tar.gz";
      response.setContentType("application/x-tar; x-gzip");
    }
    else if (gameId != null) {
      absolutePath = properties.getProperty("logLocation");
      downloadFile = "game-" + gameId + "-sim.tar.gz";
      response.setContentType("application/x-tar; x-gzip");
    }
    else if (bootId != null) {
      absolutePath = properties.getProperty("bootLocation");
      downloadFile = "game-" + bootId + "-boot.xml";
      response.setContentType("application/xml");
    }
    else if (csvName != null) {
      absolutePath = properties.getProperty("logLocation");
      downloadFile = csvName + ".csv";
      response.setContentType("text/csv");
    }
    else if (pomId != null) {
      absolutePath = properties.getProperty("pomLocation");
      downloadFile = "pom." + pomId + ".xml";
      response.setContentType("application/xml");
    }
    else if (brokerId != null) {
      absolutePath = properties.getProperty("jarLocation");
      downloadFile = "broker." + brokerId + ".jar";
      response.setContentType("application/java-archive");
    }
    else {
      return;
    }

    response.addHeader("Content-Disposition", "attachment; filename=\""
        + downloadFile + "\"");
    streamFile(response, absolutePath, downloadFile);
  }

  private void streamFile (HttpServletResponse response,
                           String absolutePath, String downloadFile)
  {
    byte[] buf = new byte[8192];
    String realPath = absolutePath + downloadFile;
    File file = new File(realPath);

    try (BufferedInputStream in =
             new BufferedInputStream(new FileInputStream(file), buf.length)) {
      ServletOutputStream out = response.getOutputStream();
      response.setContentLength((int) file.length());
      int length;
      while ((length = in.read(buf)) != -1) {
        out.write(buf, 0, length);
      }

      response.flushBuffer();
      in.close();
    }
    catch (Exception exc) {
      log.warn("File not found for downloading : " + downloadFile);
      exc.printStackTrace();
    }
  }
}
