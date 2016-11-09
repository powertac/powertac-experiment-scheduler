package org.powertac.experiment.actions;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.ExperimentSet;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.Utils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;


@ManagedBean
public class ActionExperimentSet
{
  private ExperimentSet experimentSet;

  public ActionExperimentSet ()
  {
  }

  @PostConstruct
  public void afterPropertiesSet ()
  {
    int experimentSetId = getExperimentSetId();
    if (experimentSetId < 1) {
      return;
    }

    loadExperimentSet(experimentSetId);
  }

  private int getExperimentSetId ()
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    try {
      return Integer.parseInt(facesContext.getExternalContext().
          getRequestParameterMap().get("experimentSetId"));
    }
    catch (NumberFormatException ignored) {
      if (!FacesContext.getCurrentInstance().isPostback()) {
        Utils.redirect();
      }
      return -1;
    }
  }

  private void loadExperimentSet (int experimentSetId)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.createQuery(Constants.HQL.GET_EXPERIMENT_SET_BY_ID);
      query.setInteger("experimentSetId", experimentSetId);
      experimentSet = (ExperimentSet) query.uniqueResult();

      if (experimentSet == null) {
        transaction.rollback();
        Utils.redirect();
        return;
      }

      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    finally {
      session.close();
    }
  }

  public ExperimentSet getExperimentSet ()
  {
    return experimentSet;
  }

  public List<String[]> getParamList ()
  {
    List<String[]> paramList = new ArrayList<>();

    for (Type type : experimentSet.getParamMap().getSortedKeys()) {
      paramList.add(new String[]{
          type.toString(), experimentSet.getParamMap().get(type).getValue()});
    }

    return paramList;

  }
}
