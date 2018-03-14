package org.powertac.experiment.actions;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Study;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.Utils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;


@ManagedBean
public class ActionStudy
{
  private Study study;

  public ActionStudy ()
  {
  }

  @PostConstruct
  public void afterPropertiesSet ()
  {
    int studyId = getStudyId();
    if (studyId < 1) {
      return;
    }

    loadStudy(studyId);
  }

  private int getStudyId ()
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    try {
      return Integer.parseInt(facesContext.getExternalContext().
          getRequestParameterMap().get("studyId"));
    }
    catch (NumberFormatException ignored) {
      if (!FacesContext.getCurrentInstance().isPostback()) {
        Utils.redirect();
      }
      return -1;
    }
  }

  private void loadStudy (int studyId)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.createQuery(Constants.HQL.GET_STUDIES_BY_ID);
      query.setInteger("studyId", studyId);
      study = (Study) query.uniqueResult();

      if (study == null) {
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

  public Study getStudy ()
  {
    return study;
  }

  public List<String[]> getParamList ()
  {
    List<String[]> paramList = new ArrayList<>();

    for (String name : study.getParamMap().getSortedKeys()) {
      paramList.add(new String[]{
          name, study.getParamMap().get(name).getValue()});
    }

    return paramList;

  }
}
