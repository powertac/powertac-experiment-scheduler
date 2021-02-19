# PowerTAC Orchestrator Prototype

## Installation
[[Experiment Scheduler]]

### Requirements
- Docker (https://www.docker.com/get-started)
- Java 11 (http://openjdk.java.net/)
- Maven (http://maven.apache.org/)
- MongoDB (https://www.mongodb.com/de; can also be installed via a docker container: https://hub.docker.com/_/mongo/)

At this time the Orchestrator setup is only tested on a Linux system. The following examples describe the installation and execution of the Orchestrator on Ubuntu 18.04.

### Create database user
Must match the credentials in the orchestrators configuration (application.properties; see [Configuration](#application-configuration)).
```JavaScript
use admin;  
db.auth('ADMIN_USER', 'ADMIN_PASSWORD');  
use powertac;  
db.createCollection('admin');  
db.createUser({ user: "ES_USER", pwd: "ES_PASSWORD", roles: [{ role: "readWrite", db: "powertac" }], mechanisms: [ "SCRAM-SHA-1", "SCRAM-SHA-256" ] });
```

### Web UI
TBA.

### Orchestrator
Get the orchestrator from GitHub: https://github.com/powertac/powertac-experiment-scheduler.

### Docker
*This part only concerns users working with the VPN of the University of Cologne... or for users experiencing docker network issues:*

https://blog.uni-koeln.de/rrzk-knowhow/2020/09/23/privaten-ip-adressbereich-von-docker-anpassen/#standard-ip-bereich-aendern-ab-version-18.06

## Application Configuration
```properties
# default password
# must not contain Umlauts (and perhaps other special characters)
spring.data.mongodb.username=username
spring.data.mongodb.password=password

persistence.mongodb.host=127.0.0.1
persistence.mongodb.port=27017

# declares wether the application should run in production or development mode
# values: (production|development)
# default: development
application.mode=development

# declares wether the application runs directly on the host machine or inside a container on the host machine
# values: (host|container)
# default: host
application.environment=host

# development flags
dev.keepContainersAfterRun=false

# directories relative to the application environment
directory.local.base=/var/opt/powertac
directory.local.services=${directory.local.base}services/
directory.local.brokers=${directory.local.base}brokers/
directory.local.jobs=${directory.local.base}jobs/

# directories relative to the host environment
# when running the application directly on the host machine these are the same as the application environment
# directories
directory.host.base=${directory.local.base}
directory.host.jobs=${directory.local.jobs}

# time in milliseconds to wait after each inspection of a running container
# value: number (milliseconds)
# default: 30000
container.task.inspectionInterval=2000
container.task.inspectionRetryTimeout=2000
container.task.inspectionRetryLimit=3
container.task.synchronousExecutionGracePeriod=30000
container.directory.base=/powertac/

job.policy.jobDirFormat=${directory.local.jobs}%s/

bootstrap.container.nameFormat=boot.%s
bootstrap.policy.bootstrapFileFormat=%s.bootstrap.xml

simulation.container.nameFormat=sim.%s
simulation.container.directory.base=/powertac-server/
simulation.container.defaultLogDir=/powertac-server/log/
simulation.container.defaultMessageBrokerPort=61616
simualtion.container.aliases=powertac-server
simulation.propertyFileFormat=%s.server.properties

broker.container.nameFormat=%s.%s
broker.container.defaultLogDir=/powertac-agent/log/
broker.policy.descriptorFileName=broker.json
broker.propertyFileFormat=%s.broker.properties
broker.defaultPropertiesFile=${directory.local.brokers}default.broker.properties

server.defaultImage=powertac/server:alpine
server.simulation.defaultPropertiesFile=${directory.local.services}powertac-server/server.properties
server.bootstrap.defaultPropertiesFile=${directory.local.services}powertac-server/server.properties

logging.lifecycle.defaultPattern=[%1$23s] [ %2$5s ] %3$-16s : %4$s
logging.lifecycle.defaultLogDir=${directory.local.base}log/

```

## Running the orchestrator
Start the broker by running the Maven `exec` command.

```bash
$ mvn exec:java
```

If the Orchestrator is started for the first time it will download the server images and build the broker images. Therefore it will take some time until the service is available.

## Brokers

At this time there are docker images for the following brokers:

* AgentUDE17
* CrocodileAgent16
* Maxon16
* SPOT17

Other images are included in the orchestrator service but are disabled due to compatibility issues. 2015 brokers for example simply drop out of the game after a grace period which may be caused by using a more recent server version.

