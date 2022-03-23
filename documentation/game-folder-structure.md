```
<GAME_ID>.tar.gz
	- analysis
		- ...
		- <GAME_ID>.<ANALYZER_ID>.<FORMAT_EXTENSION>
		- ...
	- boot
		- <GAME_ID>.state
		- <GAME_ID>.trace
		- (init.state)
		- (init.trace)
	- runs
		- ...
		- <RUN_ID>
			- server
				- <GAME_ID>.state
				- <GAME_ID>.trace
				- (init.state)
				- (init.trace)
			- ...
			- <BROKER_ID>
				- ...
			- ...
		- ...
	- <GAME_ID>.bootstrap.xml
	- <GAME_ID>.game.json
	- <GAME_ID>.server.properties
	- ...
	- <GAME_ID>.<BROKER_ID>.properties
	- ...
```

```
├── 655ba1f.bootstrap.properties
├── 655ba1f.bootstrap.xml
├── 655ba1f.simulation.properties
├── broker.EWIIS3.properties
├── brokers
│   ├── EWIIS3
│   │   └── logs
│   └── TUC_TAC
│       └── logs
├── broker.TUC_TAC.properties
└── log
    ├── init.state
    ├── init.trace
    ├── powertac-sim-0.state
    └── powertac-sim-0.trace
```
