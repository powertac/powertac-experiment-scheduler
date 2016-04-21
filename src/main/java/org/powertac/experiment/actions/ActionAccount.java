package org.powertac.experiment.actions;

import org.powertac.experiment.beans.User;

import javax.faces.bean.ManagedBean;


@ManagedBean
public class ActionAccount
{
  private User user = User.getCurrentUser();

  public ActionAccount ()
  {

  }

  public void editUserDetails ()
  {
    user.setEditingDetails(true);
  }

  public void saveUserDetails ()
  {
    user.save();
    user.setEditingDetails(false);
  }

  //<editor-fold desc="Setters and Getters">
  public User getUser ()
  {
    return user;
  }

  public void setUser (User user)
  {
    this.user = user;
  }
  //</editor-fold>
}
