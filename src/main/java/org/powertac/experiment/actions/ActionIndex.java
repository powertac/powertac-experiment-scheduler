package org.powertac.experiment.actions;

import org.powertac.experiment.beans.Game;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Utils;
import org.springframework.beans.factory.InitializingBean;

import javax.faces.bean.ManagedBean;
import java.util.List;


@ManagedBean
public class ActionIndex implements InitializingBean
{
  private static boolean editing;
  private List<Game> notCompleteGamesList;
  private String content;

  public ActionIndex ()
  {
  }

  public void afterPropertiesSet () throws Exception
  {
    notCompleteGamesList = Game.getNotCompleteGamesList();
  }

  public List<Game> getNotCompleteGamesList ()
  {
    return notCompleteGamesList;
  }

  public void edit ()
  {
    if (editing) {
      if (!MemStore.setIndexContent(content)) {
        Utils.growlMessage("Failed to save to DB");
        return;
      }
    }
    editing = !editing;
  }

  public void cancel ()
  {
    editing = false;
  }

  public boolean isEditing ()
  {
    return editing;
  }

  public String getContent ()
  {
    return MemStore.getIndexContent();
  }

  public void setContent (String content)
  {
    this.content = content;
  }
}