# PowerTAC Experiment Scheduler

## Installation

### Requirements
To run the Experiment Scheduler the following dependencies must be installed on your system:

- Docker: https://www.docker.com/get-started
- Java 11: http://openjdk.java.net/
- Apache Maven: http://maven.apache.org/
- MongoDB: https://www.mongodb.com/de; can also be installed via a docker container: https://hub.docker.com/_/mongo/
- NodeJS & NPM: Download (https://nodejs.org/en/download/) or install via package manager (https://nodejs.org/en/download/package-manager/)

Please make sure that there is sufficient storage space for the broker images as well as the log files.

At this time the Orchestrator setup is only tested on a Linux system. The following instructions describe the installation and execution of the Orchestrator on Ubuntu 18.04. The installation on Windows or MacOS might differ.

### Orchestrator
To install the orchestrator download or clone this repository (https://github.com/powertac/powertac-experiment-scheduler).

Now switch to the orchestrator's root directory and run the Maven `install` goal to download the required dependencies and install the orchestrator:

```bash
$ mvn install
```

Although there are more configuration options, you'll probably have to edit the MongoDB credentials as well as the base path for the Power TAC files to adapt the orchestrator to your system.

Create a file called `application.properties` in the root folder of the orchestrator and add/edit the parameters below. Please make sure that the path you enter as `directory.local.base` is writeable by the user that will run the orchestrator.

**Please be aware: the path to the orchestrator's base directory must end with a `/` or `\` (depending on you operating system).**

```properties
spring.data.mongodb.username=username
spring.data.mongodb.password=password
directory.local.base=/var/opt/powertac/
```

### Web UI
To install the web UI download or clone this repository (https://github.com/powertac/powertac-experiment-scheduler). **TODO: replace with correct link**

Switch to its root directory and run `npm install` to download and install its dependencies.

### MongoDB configuration

The experiment scheduler requires a user with access to the `powertac` database and the authentication mechanisms `SCRAM-SHA-1` and `SCRAM-SHA-256` enabled. The credentials must match with the username and password that is configured in the orchestrator's `application.properties`.

To create the user you can use the the mongodb command line client on the host (`mongo`) or in the database container (`docker exec -it MONGO_DB_CONTAINER mongo`).

Using the mongodb client you'll first have to authenticate using the admin credentials (usually registered in the `admin` database). You'll then have to switch to the `powertac` database and then create the `admin` collection as well as the orchestrator user:

```javascript
> use admin;  
> db.auth('ADMIN_USER', 'ADMIN_PASSWORD');  
> use powertac;  
> db.createCollection('admin');  
> db.createUser({ user: "ORCHESTRATOR_USER", pwd: "ORCHESTRATOR_PASSWORD", roles: [{ role: "readWrite", db: "powertac" }], mechanisms: [ "SCRAM-SHA-1", "SCRAM-SHA-256" ]});
```

### Docker
The orchestrator requires access to the Docker daemon. Therefore the user that will be running the orchestrator needs permissions. Please refer to the Docker documentation for details: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user.

## Running the scheduler
Start the orchestrator by running the Maven `exec` command. The orchestrator will listen by default to port 8080.

```bash
$ mvn exec:java
```

If the orchestrator is started for the first time it will download the server images and build the broker images. Therefore it will take some time until the service is available.

Once the orchestrator is ready you can start the web ui by running the `npm run` command:

```bash
$ npm run dev
```

The web UI will be reachable by default via http://localhost:9000. You can run the web UI on a different port by using the following command, where `<PORT>` represents the desired port:

```bash
$ npm run serve -- --port <PORT> --mode development
```

### Security concerns

There are currently no security measures in place to restrict access to the web UI or the orchestrator's REST API. Users will however only be able to queue new experiments and games.

User management and access restrictions will be added in one of the first updates.

To restrict access for the time being, we recommend blocking the orchestrator's port (8080) for incoming traffic except for the IPs that should be able to queue experiments and games.

## Brokers

The orchestrator currently uses json files within the orchestrator's working directory to manage brokers. Options to manage brokers via the web UI will be added in the first update.

At this time the docker images for the following brokers are created during the orchestrator's first startup:

* TUC_TAC_2020
* AgentUDE17
* SPOT17
* CrocodileAgent16
* Maxon16

An image for the EWIIS3 broker is available for download via Docker Hub. You can pull the image with the following command and add the broker as described below.

```bash
$ docker pull is3cologne/ewiis3:2020-latest
```

### Adding broker images

To make a broker image available for use with the Experiment Scheduler, you'll need to add a new directory in the experiment scheduler broker directory as well as a `broker.json` file within this directory (the broker directory is usually a subdirectory of the Experiment Scheduler base directory configured in the `application.properties` file).


```bash
$ cd /path/to/powertac/directory/brokers
$ mkdir my_broker
```

For the time being, the `broker.json` file only contains the broker's name as well as the image tag given to the broker image.

```json
{  
  "name": "MyBroker",  
  "image": "my_broker:latest"
}
```

### Removing broker images

To remove a broker from the set of available brokers you can either remove the broker's directory or set the `disabled` field in the `broker.json`.

```json
{  
  "name": "MyBroker",  
  "image": "my_broker:latest",
  "disabled": true
}
```

## Known issues

- The UI currenctly doesn't react correctly to some changes to experiments and game statuses. Reloading the page should do the trick.
- In some cases when a game's containers have been created but not started and the orchestrator shuts down, the created containers are not removed and the orchestrator will throw an error due to this conflict. When this happens, shut down the orchestrator, remove the containers manually (`docker rm`) and restart the orchestrator.