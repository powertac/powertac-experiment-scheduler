package org.powertac.experiment.services;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.jobs.RunBoot;
import org.powertac.experiment.jobs.RunSim;
import org.powertac.experiment.models.Parameter;
import org.powertac.experiment.states.ExperimentState;
import org.powertac.experiment.states.GameState;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


@Service("scheduler")
@ManagedBean
public class Scheduler implements InitializingBean
{
  private static Logger log = Utils.getLogger();

  @Autowired
  private Properties properties;

  private Timer schedulerTimer = null;
  private long schedulerInterval;

  private List<Experiment> runningExperiments;
  private long lastSchedulerRun = 0;

  public Scheduler ()
  {
    runningExperiments = new ArrayList<>();
  }

  public static Scheduler getScheduler ()
  {
    return (Scheduler) SpringApplicationContext.getBean("scheduler");
  }

  public void afterPropertiesSet () throws Exception
  {
    lazyStart();
  }

  private void lazyStart ()
  {
    schedulerInterval =
        properties.getPropertyInt("scheduler.schedulerInterval");

    Timer t = new Timer();
    TimerTask tt = new TimerTask()
    {
      @Override
      public void run ()
      {
        startScheduler();
      }
    };
    t.schedule(tt, 3000);
  }

  private synchronized void startScheduler ()
  {
    if (schedulerTimer != null) {
      log.warn("Scheduler already running");
      return;
    }

    log.info("Starting Scheduler...");

    // Trigger a load of all types
    Parameter.getAvailableBaseParams();

    lastSchedulerRun = System.currentTimeMillis();

    TimerTask schedulerTimerTask = new TimerTask()
    {
      @Override
      public void run ()
      {
        // Empty line to make logs more readable
        log.info(System.getProperty("line.separator"));
        try {
          MemStore.getNameMapping(false);
          List<Game> notCompleteGames = Game.getNotCompleteGamesList();
          List<Machine> freeMachines = Machine.checkMachines();
          RunSim.startRunnableGames(getRunningExperimentIds(), notCompleteGames, freeMachines);
          RunBoot.startBootableGames(getRunningExperimentIds(), notCompleteGames, freeMachines);
          checkWedgedBoots(notCompleteGames);
          checkWedgedSims(notCompleteGames);
          lastSchedulerRun = System.currentTimeMillis();
        }
        catch (Exception e) {
          log.error("Severe error in SchedulerTimer!");
          e.printStackTrace();
        }
      }
    };

    schedulerTimer = new Timer();
    schedulerTimer.schedule(schedulerTimerTask, new Date(), schedulerInterval);
  }

  private void stopScheduler ()
  {
    if (schedulerTimer != null) {
      schedulerTimer.cancel();
      schedulerTimer.purge();
      schedulerTimer = null;
      log.info("Stopping Scheduler...");
    }
    else {
      log.warn("SchedulerTimer Already Stopped");
    }
  }

  public boolean restartScheduler ()
  {
    if ((System.currentTimeMillis() - lastSchedulerRun) < 55000) {
      stopScheduler();
      startScheduler();
      return true;
    }
    else {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public void loadExperiments (List<Integer> experimentIds)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();

    runningExperiments = new ArrayList<>();
    try {
      Set<Experiment> allExperiments = new HashSet<>((List<Experiment>)
              session.createQuery(Constants.HQL.GET_EXPERIMENTS).list());
      for (Experiment experiment : allExperiments) {
        if (experimentIds.contains(experiment.getExperimentId()) &&
            experiment.getState() != ExperimentState.complete) {
          runningExperiments.add(experiment);
        }
      }
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }

  public void unloadExperiments (boolean logInfo)
  {
    if (logInfo) {
      for (Experiment experiment : runningExperiments) {
        log.info("Unloading Round " + experiment.getExperimentId());
      }
      log.info("All rounds are unloaded");
    }
    runningExperiments.clear();
  }

  // This function removes the given round from 'runningExperiments'.
  public void unloadExperiment (Integer experimentId)
  {
    Experiment experiment;
    Iterator<Experiment> experimentIterator = runningExperiments.listIterator();

    while (experimentIterator.hasNext()) {
      experiment = experimentIterator.next();
      if (experiment.getExperimentId() == experimentId) {
        experimentIterator.remove();
      }
    }
  }

  private void checkWedgedBoots (List<Game> notCompleteGames)
  {
    log.info("SchedulerTimer Looking for Wedged Bootstraps");

    for (Game game : notCompleteGames) {
      if (!game.getState().equals(GameState.boot_in_progress)) {
        continue;
      }

      long wedgedDeadline =
          properties.getPropertyInt("scheduler.bootstrapWedged");
      long nowStamp = Utils.offsetDate().getTime();
      long startStamp = game.getParamMap().getStartTime().getTime();
      long minStamp = startStamp + wedgedDeadline;
      long maxStamp = minStamp + schedulerInterval;

      if (nowStamp > minStamp && nowStamp < maxStamp) {
        String msg = String.format(
            "Bootstrapping of game %s seems to take too long : %s seconds",
            game.getGameId(), ((nowStamp - startStamp) / 1000));
        log.error(msg);
        Utils.sendMail("Bootstrap seems stuck", msg,
            properties.getProperty("scheduler.mailRecipient"));
        properties.addErrorMessage(msg);
      }
    }
    log.debug("SchedulerTimer No Bootstraps seems Wedged");
  }

  private void checkWedgedSims (List<Game> notCompleteGames)
  {
    log.info("SchedulerTimer Looking for Wedged Sims");

    for (Game game : notCompleteGames) {
      if (!game.getState().isRunning()) {
        continue;
      }

      long wedgedDeadline =
          properties.getPropertyInt("scheduler.simWedged");
      long nowStamp = Utils.offsetDate().getTime();
      long startStamp = game.getParamMap().getStartTime().getTime();
      long minStamp = startStamp + wedgedDeadline;
      long maxStamp = minStamp + schedulerInterval;

      if (nowStamp > minStamp && nowStamp < maxStamp) {
        String msg = String.format(
            "Sim of game %s seems to take too long : %s seconds",
            game.getGameId(), ((nowStamp - startStamp) / 1000));
        log.error(msg);
        Utils.sendMail("Sim seems stuck", msg,
            properties.getProperty("scheduler.mailRecipient"));
        properties.addErrorMessage(msg);
      }
    }
    log.debug("SchedulerTimer No Sim seems Wedged");
  }

  public boolean noExperimentsScheduled ()
  {
    return runningExperiments == null || runningExperiments.size() == 0;
  }

  public boolean isRunning ()
  {
    return schedulerTimer != null;
  }

  public List<Experiment> getRunningExperiments ()
  {
    return runningExperiments;
  }

  public String getRunningString ()
  {
    StringBuilder builder = new StringBuilder();
    for (Experiment experiment : runningExperiments) {
      builder.append(experiment.getName()).append(", ");
    }

    return builder.substring(0, Math.max(0, builder.length() - 2));
  }

  public List<Integer> getRunningExperimentIds ()
  {
    List<Integer> runningExperimentIds = new ArrayList<>();
    for (Experiment experiment : runningExperiments) {
      runningExperimentIds.add(experiment.getExperimentId());
    }
    return runningExperimentIds;
  }

  @PreDestroy
  private void cleanUp () throws Exception
  {
    log.info("Spring Container is destroyed! Scheduler clean up");

    stopScheduler();
  }

  //<editor-fold desc="Setters and Getters">
  public long getSchedulerInterval ()
  {
    return schedulerInterval;
  }

  public void setSchedulerInterval (long schedulerInterval)
  {
    this.schedulerInterval = schedulerInterval;
  }

  public String getLastSchedulerRun ()
  {
    if (lastSchedulerRun == 0) {
      return "";
    }
    else {
      return String.format(" : ran %s secs ago",
          (int) (System.currentTimeMillis() - lastSchedulerRun) / 1000);
    }
  }
  //</editor-fold>
}