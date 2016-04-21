package org.powertac.experiment.constants;

import org.powertac.experiment.states.GameState;


public class Constants
{
  public static class Prop
  {
    public static final String weatherServerURL =
        "server.weatherService.serverUrl = ";
    public static final String weatherLocation =
        "server.weatherService.weatherLocation = ";
    public static final String startTime =
        "common.competition.simulationBaseTime = ";
    public static final String jms =
        "server.jmsManagementService.jmsBrokerUrl = ";
    public static final String serverFirstTimeout =
        "server.competitionControlService.firstLoginTimeout = ";
    public static final String serverTimeout =
        "server.competitionControlService.loginTimeout = ";
    public static final String minTimeslot =
        "common.competition.minimumTimeslotCount = ";
    public static final String expectedTimeslot =
        "common.competition.expectedTimeslotCount = ";
    public static final String timezoneOffset =
        "common.competition.timezoneOffset = ";

    public static final String tourneyName =
        "samplebroker.core.powerTacBroker.tourneyName = ";
    public static final String brokerUsername =
        "samplebroker.core.powerTacBroker.username = ";
    public static final String tourneyUrl =
        "samplebroker.core.powerTacBroker.tourneyUrl = %sfaces/brokerLogin.jsp";
    public static final String authToken =
        "samplebroker.core.powerTacBroker.authToken = ";
  }

  public static class Rest
  {
    // Possible Rest Parameters for Broker Login
    public static final String REQ_PARAM_AUTH_TOKEN = "authToken";
    public static final String REQ_PARAM_JOIN = "requestJoin";
    public static final String REQ_PARAM_TYPE = "type";

    // Possible Rest Paramenters for Server Interface
    public static final String REQ_PARAM_STATUS = "status";
    public static final String REQ_PARAM_GAMEID = "gameId";
    public static final String REQ_PARAM_GAMENAME = "gameName";
    public static final String REQ_PARAM_BROKERID = "brokerId";
    public static final String REQ_PARAM_ACTION = "action";
    public static final String REQ_PARAM_FILENAME = "fileName";
    public static final String REQ_PARAM_MESSAGE = "message";
    public static final String REQ_PARAM_BOOT = "boot";
    public static final String REQ_PARAM_SEED = "seed";
    public static final String REQ_PARAM_HEARTBEAT = "heartbeat";
    public static final String REQ_PARAM_GAMERESULTS = "gameresults";
    public static final String REQ_PARAM_GAMELENGTH = "gameLength";
    public static final String REQ_PARAM_STANDINGS = "standings";
    public static final String REQ_PARAM_ELAPSED_TIME = "elapsedTime";
    public static final String REQ_PARAM_AGENT_STATUS = "agentStatus";

    // Possible Rest Parameters for pom service
    public static final String REQ_PARAM_POM_ID = "pomId";
  }

  public static class HQL
  {
    public static final String GET_USERS =
        "FROM User AS user ";

    public static final String GET_USER_BY_NAME =
        "FROM User AS user "
            + "WHERE user.userName =:userName ";

    public static final String GET_POMS =
        "FROM Pom AS pom "
            + "ORDER BY pom.pomId DESC ";

    public static final String GET_LOCATIONS =
        "FROM Location AS location ";

    public static final String GET_LOCATION_BY_NAME =
        "FROM Location AS location WHERE location.location =:locationName ";

    public static final String GET_MACHINES =
        "FROM Machine AS machine ";

    public static final String GET_EXPERIMENT_SETS =
        "FROM ExperimentSet AS experimentSet "
            + "LEFT JOIN FETCH experimentSet.parameterMap AS parameterMap ";

    public static final String GET_EXPERIMENTS =
        "FROM Experiment AS experiment "
            + "LEFT JOIN FETCH experiment.parameterMap AS parameterMap "
            + "LEFT JOIN FETCH experiment.gameMap AS gameMap "
            + "LEFT JOIN FETCH gameMap.agentMap "
            + "LEFT JOIN FETCH gameMap.parameterMap ";

    public static final String GET_GAME_BY_ID =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.parameterMap "
            + "LEFT JOIN FETCH game.experiment as experiment "
            + "LEFT JOIN FETCH experiment.parameterMap "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap as agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "WHERE game.gameId =:gameId";

    public static final String GET_GAMES_NOT_COMPLETE =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.parameterMap "
            + "LEFT JOIN FETCH game.experiment as experiment "
            + "LEFT JOIN FETCH experiment.parameterMap "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "WHERE NOT game.state='" + GameState.game_complete + "' ";

    public static final String GET_GAMES_READY =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.experiment as experiment "
            + "LEFT JOIN FETCH experiment.parameterMap "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "WHERE game.state='" + GameState.game_ready + "' ";

    public static final String GET_GAMES_COMPLETE =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.experiment "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "WHERE game.state='" + GameState.game_complete + "' ";

    public static final String GET_BROKERS =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.agentMap as agentMap ";

    public static final String GET_BROKER_BY_ID =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.agentMap as agentMap "
            + "LEFT JOIN FETCH agentMap.game "
            + "LEFT JOIN FETCH agentMap.broker as broker2 "
            + "WHERE broker.brokerId =:brokerId ";

    public static final String GET_BROKER_BY_NAME =
        "FROM Broker AS broker "
            + "WHERE brokerName =:brokerName ";

    public static final String GET_BROKER_BY_BROKERAUTH =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.agentMap as agentMap "
            + "LEFT JOIN FETCH agentMap.game as game "
            + "LEFT JOIN FETCH game.experiment "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH agentMap.broker as broker2 "
            + "WHERE broker.brokerAuth =:brokerAuth ";

    public static final String GET_CONFIG =
        "FROM Config AS config "
            + "WHERE config.configKey =:configKey ";
  }
}
