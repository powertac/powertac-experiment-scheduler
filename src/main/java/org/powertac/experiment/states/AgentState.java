package org.powertac.experiment.states;


public enum AgentState
{
  pending, in_progress, complete;

  public boolean isPending ()
  {
    return this.equals(pending);
  }

  public boolean isInProgress ()
  {
    return this.equals(in_progress);
  }
}
