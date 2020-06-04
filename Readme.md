# TheHand

## Requirements
-   PostgresSQL [download](https://www.postgresql.org/download/)
-   Java SDK 11 or Superior [download](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
-   Sbt [download](https://www.scala-sbt.org/download.html)

### Setup DB
Create a user and database
[link for example](https://www.postgresql.org/docs/8.0/static/sql-createuser.html)

### Config
You can find an example in /conf/application.conf

Test config for running in memory
```conf
testConfig = {
  connectionPool      = disabled
  url                 = "jdbc:h2:mem:testdb"
  driver              = "org.h2.Driver"
  keepAliveConnection = true
}
```

Production config
```conf
dbconfig = {
  connectionPool = disabled
  url = "jdbc:postgresql:DATABASENAME?user=USERDBNAME&password=PASSDB"
  profile = ""
  driver = "org.postgresql.Driver"
  keepAliveConnection = true
  maxActive = 4
  maxConnections = 40
  numThreads = 10
}
```

#### Project Setup
Each project has a distinct configuration with the repository connection data and connection to the database.

It is important to switch to each project ```database_suffix =" demo_ "```, to keep each project separate using suffixes.
```conf
projectDemo = {
  user = "YOUR USER"
  pass = "YOUR PASS"
  url = "YOUR SVN URL"
  task_model = {
    patternParser = "(#\\d)\\d+" //task or fix #NUMBER
    patternSplit = "#" //task or fix #NUMBER
    separator = ""
  }
}
```

Defines how the information about the tasks is extracted
```conf
patternParser = "(#\\d)\\d+" //task or fix #NUMBER
patternSplit = "#" //task or fix #NUMBER
separator = ""
```

##### Target Configuration
For agile flow data aggregation using [TargetProcess] (www.targetprocess.com)
Currently TheHand does not have support for other means of authentication.
```conf
target = {
  user = "YOUR USER"
  pass = "YOUR PASS"
  url = "YOUR URL"
}
```
 
### Devel
```bash
> sbt "-Dconfig.file=/... path .../application.conf
$> run
```

Examples to check the config
```bash
> curl -v -X POST http://ip:9000/YOUR_MAGIC_SECRET/
> curl -v -X POST http://ip:9000/boot/YOUR_MAGIC_SECRET/
> curl -v -X POST -H 'X-API-Key:AbCdEfGhIjK1' http://ip:9000/api/v1/update/suffix_table
```

#### Test
```bash
> sbt test
```

#### Run
Run this on __production__:
```bash
> sbt universal:packageBin
```

And copy the generate 'package' from target.
```bash
> ./path_generated/bin/thehand
```

> __Note:__ Remember to open port 9000 on server

Also you can run the python scripts:

##### Python 2
```bash
 pip install requests
 python boot.py
 python update_auto.py
 python LOC/count_lines_json.py
 python generate_reports
```
> __Note:__ Remember to change the configuration.py to your needs.

