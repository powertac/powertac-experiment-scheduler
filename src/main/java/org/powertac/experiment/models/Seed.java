package org.powertac.experiment.models;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.log4j.Logger;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;
import org.springframework.util.FileSystemUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Seed
{
  private static Logger log = Utils.getLogger();

  private static Properties properties = Properties.getProperties();
  private static String seedsLocation = properties.getProperty("seedLocation");

  // Return ids of the seed files
  public static List<Integer> getSeedIds ()
  {
    File f = new File(seedsLocation);
    String[] files = f.list();
    if (files == null) {
      return new ArrayList<>();
    }

    List<Integer> seedIds = new ArrayList<>();
    for (String file : files) {
      seedIds.add(getSeedId(file));
    }
    return seedIds;
  }

  private static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    }
    catch (Exception ignored) {}
  }

  public static List<Integer> retrieveSeeds (String seedList)
  {
    String[] seedUrls = seedList.split("\n");
    if (seedUrls.length == 0) {
      return null;
    }

    int seedId = getNextId();
    List<Integer> seedIds = new ArrayList<>();
    for (String seedUrl : seedUrls) {
      if (isExistingSeedId(seedUrl)) {
        seedIds.add(getSeedId(seedUrl));
      }
      // Download the seed / state file, store them on file system
      else {
        boolean downloaded = retrieveFile(seedUrl, seedId);
        if (downloaded) {
          seedIds.add(seedId++);
        }
      }
    }

    return seedIds;
  }

  private static int getNextId ()
  {
    int lastId = 0;
    File f = new File(seedsLocation);

    String[] fileNames = f.list();
    if (fileNames != null) {
      for (String fileName : fileNames) {
        lastId = Math.max(lastId, getSeedId(fileName));
      }
    }
    return lastId + 1;
  }

  public static boolean isExistingSeedId (String seedUrl)
  {
    int seedId = getSeedId(seedUrl);
    return getSeedIds().contains(seedId);
  }

  private static int getSeedId (String name)
  {
    try {
      return Integer.valueOf(name.replaceAll("[^\\d]", ""));
    }
    catch (Exception ignored) {
      return -1;
    }
  }

  private static boolean retrieveFile (String seedUrl, int seedId)
  {
    Path tmpDir = null;
    try {
      tmpDir = Files.createTempDirectory(null);
      String saveFilePath = downloadFile(seedUrl, tmpDir);

      // Unpack the tarball if needed
      if (saveFilePath != null && saveFilePath.endsWith(".tar.gz")) {
        saveFilePath = unpackTarGz(saveFilePath, tmpDir.toString());
      }

      // Move the state file (and state file only)
      if (saveFilePath != null && saveFilePath.endsWith(".state")) {
        String seedPath = seedsLocation + "seed." + seedId + ".state";

        try {
          Files.move(new File(saveFilePath).toPath(),
              new File(seedPath).toPath());
          return true;
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (tmpDir != null) {
        FileSystemUtils.deleteRecursively(tmpDir.toFile());
      }
    }
    return false;
  }

  private static String downloadFile (String seedUrl, Path saveDir)
      throws IOException
  {
    log.info("Downloading : " + seedUrl);
    URL url = new URL(seedUrl);
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    int responseCode = httpConn.getResponseCode();

    // Check HTTP response code first
    if (responseCode != HttpURLConnection.HTTP_OK) {
      log.info("Not downloaded, response code : " + responseCode);
      httpConn.disconnect();
      return null;
    }

    String fileName = getFilename(httpConn, seedUrl);
    String saveFilePath = saveDir + File.separator + fileName;

    InputStream inputStream = httpConn.getInputStream();
    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

    int bytesRead = -1;
    byte[] buffer = new byte[4096];
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }

    outputStream.close();
    inputStream.close();
    httpConn.disconnect();

    return saveFilePath;
  }

  private static String getFilename (HttpURLConnection httpConn, String fileURL)
  {
    String disposition = httpConn.getHeaderField("Content-Disposition");
    if (disposition != null) {
      int index = disposition.indexOf("filename=");
      if (index > 0) {
        return disposition.substring(index + 9);
      }
    }

    return fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
  }

  private static String unpackTarGz (String source, String destDir)
  {
    if (source == null || destDir == null) {
      return null;
    }
    if (!destDir.endsWith(File.separator)) {
      destDir += File.separator;
    }

    TarArchiveEntry entry;
    try {
      TarArchiveInputStream tarIn = new TarArchiveInputStream(
          new GzipCompressorInputStream(new BufferedInputStream(
              new FileInputStream(source))));

      String fileName = null;
      while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
        // Ignore boot stuff, init stuff and trace/xml stuff
        String path = entry.getName();
        if (!path.startsWith("log/") ||
            path.startsWith("log/init.") ||
            !path.endsWith(".state")) {
          continue;
        }

        fileName = destDir + path.substring(path.lastIndexOf(File.separator) + 1);
        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream dest = new BufferedOutputStream(fos, 2048);

        int count;
        byte data[] = new byte[2048];
        while ((count = tarIn.read(data, 0, 2048)) != -1) {
          dest.write(data, 0, count);
        }
        dest.close();
      }

      tarIn.close();
      return fileName;
    }
    catch (IOException ioe) {
      return null;
    }
  }
}