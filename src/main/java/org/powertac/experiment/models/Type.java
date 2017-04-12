package org.powertac.experiment.models;

import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Pom;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


// TODO This isn't perfect for configuring
public enum Type
{
  brokers(Integer.class, null, "Comma separated list of broker ids", false),
  pomId(Integer.class),
  bootstrapId(Integer.class),
  seedId(Integer.class, null, "If not set, no seed file will be used"), // TODO Fix this
  location(String.class),
  simStartDate(String.class, null,
      "If not set, a random value will be used for all games in the set"), // TODO Fix this
  createTime(String.class),
  multiplier(Integer.class, null, "Games per experiment"),
  gameLength(Integer.class, null, "If not set, this will be randomized"),
  startTime(String.class),

  // TODO Get presets from server.properties
  accounting_accountingService_bankInterest(Double.class, null),
  accounting_accountingService_maxInterest(Double.class, 0.12),
  accounting_accountingService_minInterest(Double.class, 0.04),
  auctioneer_auctionService_defaultClearingPrice(Double.class, 40),
  auctioneer_auctionService_defaultMargin(Double.class, 0.2),
  auctioneer_auctionService_sellerSurplusRatio(Double.class, 0.5),
  balancemkt_balancingMarketService_balancingCost(Double.class, null),
  balancemkt_balancingMarketService_balancingCostMax(Double.class, -0.006),
  balancemkt_balancingMarketService_balancingCostMin(Double.class, -0.002),
  balancemkt_balancingMarketService_defaultSpotPrice(Double.class, 75),
  balancemkt_balancingMarketService_pMinusPrime(Double.class, -0.000001),
  balancemkt_balancingMarketService_pPlusPrime(Double.class, 0.000001),
  balancemkt_balancingMarketService_rmPremium(Double.class, 1.8),
  balancemkt_balancingMarketService_settlementProcess(String.class, "static"),
  common_competition_bootstrapDiscardedTimeslots(Integer.class, 24),
  common_competition_bootstrapTimeslotCount(Integer.class, 336),
  common_competition_deactivateTimeslotsAhead(Integer.class, 1),
  common_competition_expectedTimeslotCount(Integer.class, 1440),
  common_competition_latitude(Integer.class, 45),
  common_competition_minimumOrderQuantity(Double.class, 0.01),
  common_competition_minimumTimeslotCount(Integer.class, 1400),
  common_competition_simulationBaseTime(String.class, "40096"),
  common_competition_simulationTimeslotSeconds(Integer.class, 5),
  common_competition_timeslotLength(Integer.class, 60),
  common_competition_timeslotsOpen(Integer.class, 24),
  common_competition_timezoneOffset(Integer.class, -6),
  distributionutility_distributionUtilityService_distributionFee(Double.class, -0.01),
  distributionutility_distributionUtilityService_distributionFeeMax(Double.class, -0.03),
  distributionutility_distributionUtilityService_distributionFeeMin(Double.class, -0.003),
  distributionutility_distributionUtilityService_useCapacityFee(Double.class, 1),
  distributionutility_distributionUtilityService_useMeterFee(Double.class, 1),
  distributionutility_distributionUtilityService_useTransportFee(Double.class, 0),
  du_defaultBrokerService_buyLimitPriceMax(Double.class, -5),
  du_defaultBrokerService_buyLimitPriceMin(Double.class, -100),
  du_defaultBrokerService_consumptionRate(Double.class, -0.5),
  du_defaultBrokerService_initialBidKWh(Double.class, 1000),
  du_defaultBrokerService_productionRate(Double.class, 0.01),
  du_defaultBrokerService_sellLimitPriceMax(Double.class, 30),
  du_defaultBrokerService_sellLimitPriceMin(Double.class, 0.1),
  // TODO How do we handle String arrays?
  /* genco_cpGenco_coefficients(String.class, "[0.005,0.02,14]"), */
  genco_cpGenco_minQuantity(Double.class, 150),
  householdcustomer_householdCustomerService_configFile1(String.class, "VillageType1.properties"),
  officecomplexcustomer_officeComplexCustomerService_configFile1(String.class, "OfficeComplexType1.properties"),
  // server_bootstrapDataFile(String.class, "boot-data.xml"),
  server_competitionControlService_bootstrapTimeslotMillis(Long.class, 400),
  //server_competitionControlService_loginTimeout(Integer.class, 0),
  server_competitionControlService_stackTraceDepth(Integer.class, 6),
  //server_jmsManagementService_jmsBrokerUrl(String.class, "tcp://localhost:61616"),
  server_logfileSuffix(String.class, "default"),
  server_simulationClockControl_minAgentWindow(Integer.class, 2000),
  server_weatherService_blocking(Boolean.class, 0),
  server_weatherService_forecastHorizon(Integer.class, 24),
  server_weatherService_serverUrl(String.class, "http://wolf31.ict.eur.nl:8080/WeatherServer/faces/index.xhtml"),
  //server_weatherService_weatherLocation(String.class, "rotterdam"),
  server_weatherService_weatherReqInterval(Integer.class, 24),
  tariffmarket_tariffMarketService_maxPublicationFee(Double.class, -5000),
  tariffmarket_TariffMarketService_maxRevocationFee(Double.class, -500),
  tariffmarket_tariffMarketService_minPublicationFee(Double.class, -1000),
  tariffmarket_TariffMarketService_minRevocationFee(Double.class, -100),
  tariffmarket_tariffMarketService_publicationFee(Double.class, null),
  tariffmarket_tariffMarketService_publicationInterval(Integer.class, 6),
  tariffmarket_tariffMarketService_publicationOffset(Integer.class, 1),
  tariffmarket_TariffMarketService_revocationFee(Double.class, null);

  public Class clazz;
  public String preset;
  public String description;
  public boolean exclusive = true;

  Type (Object... attributes)
  {
    if (attributes.length > 0) {
      this.clazz = (Class) attributes[0];
    }
    if (attributes.length > 1 && attributes[1] != null) {
      this.preset = attributes[1].toString();
    }
    if (attributes.length > 2 && attributes[2] != null) {
      this.description = attributes[2].toString();
    }
    if (attributes.length > 3) {
      this.exclusive = (Boolean) attributes[3];
    }
  }

  public String getDefault ()
  {
    if (this == brokers || this == pomId) {
      return preset.split(" ")[0];
    }

    if (this == bootstrapId || this == seedId || this == location) {
      String result = preset.split(",")[0];

      // Needed for boot files
      result = result.replace("boot.", "").replace(".xml", "");

      // Needed for seed files
      result = result.replace("seed.", "").replace(".state", "");

      return result;
    }

    if (this == simStartDate) {
      return preset.split(" - ")[0];
    }

    if (this == multiplier) {
      return "2";
    }

    if (this == server_weatherService_serverUrl) {
      return preset;
    }

    return "";
  }

  public void setPreset (String preset)
  {
    this.preset = preset;
  }

  public static Set<Type> getStudyTypes ()
  {
    lazyLoad();

    Set<Type> setTypes = new LinkedHashSet<>(Arrays.asList(Type.values()));
    setTypes.remove(startTime);
    return setTypes;
  }

  public static Set<Type> getExperimentTypes ()
  {
    Set<Type> experimentTypes = new LinkedHashSet<>(Arrays.asList(Type.values()));
    experimentTypes.remove(createTime);
    experimentTypes.remove(startTime);
    return experimentTypes;
  }

  public static Set<Type> getGameTypes ()
  {
    Set<Type> gameTypes = new LinkedHashSet<>(Arrays.asList(Type.values()));
    gameTypes.remove(brokers);
    gameTypes.remove(createTime);
    gameTypes.remove(startTime);
    return gameTypes;
  }

  private static boolean lazyLoaded;

  private static void lazyLoad ()
  {
    if (lazyLoaded) {
      return;
    }

    lazyLoaded = true;

    List<Broker> brokerList = Broker.getBrokerList();
    if (brokerList.size() > 0) {
      brokers.setPreset(brokerList.toString().replace("[", "").replace("]", ""));
    }

    List<Pom> pomList = Pom.getPomList();
    if (pomList.size() > 0) {
      pomId.setPreset(pomList.toString().replace("[", "").replace("]", ""));
    }

    List<String> bootList = Bootstrap.getBootstraps();
    if (bootList.size() > 0) {
      bootstrapId.setPreset(bootList.stream().map(
          p -> p.replace("bootstrap-", "").replace(".xml", ""))
          .collect(Collectors.joining(", ")));
    }

    List<String> seedList = Seed.getSeeds();
    if (seedList.size() > 0) {
      seedId.setPreset(seedList.stream().map(
          p -> p.replace("powertac-sim-", "").replace(".state", ""))
          .collect(Collectors.joining(", ")));
    }

    List<Location> locList = Location.getLocationList();
    if (locList.size() > 0) {
      location.setPreset(locList.stream().map(Location::getLocation)
          .collect(Collectors.joining(", ")));
    }
    simStartDate.setPreset(locList.stream().map(Location::getRange)
        .collect(Collectors.joining(", ")));
  }
}