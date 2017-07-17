package org.powertac.experiment.states;

import java.util.EnumSet;


public enum GameState
{
  /*
  - Boot
    Games are initially set to boot_pending.
    When the job is sent to Jenkins, the TM sets it to in_progress.
    When done the Jenkins script sets it to complete or failed, depending on
    the resulting boot file. When the TM isn't able to send the job to
    Jenkins, the game is set to failed as well.

  - Game
    When the job is sent to Jenkins, the TM sets it to game_pending.
    When the sim is ready, the sim sets the game to game_ready.
    (This is done before the game is actually started.
    When all the brokers are logged in (or login timeout occurs), the sim sets
    the game to in_progress.

    When the sim stops, the Jenkins script sets the game to complete.
    game_failed occurs when the script encounters problems downloading the POM-
    or boot-file, or when RunGame has problems sending the job to jenkins.
  */

  boot_pending, boot_in_progress, boot_complete, boot_failed,
  game_pending, game_ready, game_in_progress, game_complete, game_failed;

  // Some convenient collections
  public static final EnumSet<GameState> hasBootstrap = EnumSet.of(
      boot_complete,
      game_pending,
      game_ready,
      game_in_progress,
      game_complete);

  public static final EnumSet<GameState> isRunning = EnumSet.of(
      game_pending,
      game_ready,
      game_in_progress);

  public static final EnumSet<GameState> freeMachine = EnumSet.of(
      boot_failed,
      boot_complete,
      game_failed,
      game_complete);

  // Some convenient functions
  public boolean isRunning ()
  {
    return isRunning.contains(this);
  }

  public boolean isFailed ()
  {
    return equals(boot_failed) || equals(game_failed);
  }
}