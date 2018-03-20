package org.powertac.experiment.beans;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.Utils;

import javax.faces.bean.ManagedBean;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;


@ManagedBean
@Entity
@Table(name = "brokers")
public class Broker implements Serializable
{
  private static Logger log = Utils.getLogger();

  private Integer brokerId;
  private String brokerName;
  private String brokerAuth;
  private String shortDescription;

  private Map<Integer, Agent> agentMap = new HashMap<>();

  public Broker ()
  {
  }

  @Override
  public String toString ()
  {
    return brokerId + " (" + brokerName +")";
  }

  //<editor-fold desc="Collections">
  @SuppressWarnings("unchecked")
  public static List<Broker> getBrokerList ()
  {
    List<Broker> brokers = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.createQuery(Constants.HQL.GET_BROKERS);
      brokers = (List<Broker>) query.
          setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();

    return brokers;
  }

  @SuppressWarnings("unchecked")
  public static List<Broker> getBrokersByIds (Session session, String arrayString)
  {
    List<Integer> brokerIds = Utils.valueList(arrayString)
        .stream().map(Integer::valueOf).collect(Collectors.toList());

    Query query = session.createQuery(Constants.HQL.GET_BROKERS);
    List<Broker> Allbrokers = (List<Broker>) query.
        setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

    List<Broker> brokers = new ArrayList<>();
    for (Broker broker : Allbrokers) {
      if (brokerIds.contains(broker.getBrokerId())) {
        brokers.add(broker);
      }
    }

    return brokers;
  }

  public boolean save ()
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.save(this);
      transaction.commit();
      return true;
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      return false;
    }
    finally {
      session.close();
    }
  }

  public String update ()
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.update(this);
      transaction.commit();
      return null;
    }
    catch (ConstraintViolationException cve) {
      transaction.rollback();
      return cve.getMessage();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      return "Error updating broker";
    }
    finally {
      session.close();
    }
  }

  @OneToMany
  @JoinColumn(name = "brokerId")
  @MapKey(name = "gameId")
  public Map<Integer, Agent> getAgentMap ()
  {
    return agentMap;
  }

  public void setAgentMap (Map<Integer, Agent> agentMap)
  {
    this.agentMap = agentMap;
  }
  //</editor-fold>

  //<editor-fold desc="Bean Setters and Getters">
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "brokerId", unique = true, nullable = false)
  public Integer getBrokerId ()
  {
    return brokerId;
  }

  public void setBrokerId (Integer brokerId)
  {
    this.brokerId = brokerId;
  }

  @Column(name = "brokerName", nullable = false)
  public String getBrokerName ()
  {
    return brokerName;
  }

  public void setBrokerName (String brokerName)
  {
    this.brokerName = brokerName;
  }

  @Column(name = "brokerAuth", unique = true, nullable = false)
  public String getBrokerAuth ()
  {
    return brokerAuth;
  }

  public void setBrokerAuth (String brokerAuth)
  {
    this.brokerAuth = brokerAuth;
  }

  @Column(name = "brokerShort", nullable = false)
  public String getShortDescription ()
  {
    return shortDescription;
  }

  public void setShortDescription (String shortDescription)
  {
    this.shortDescription = shortDescription;
  }
  //</editor-fold>
}
