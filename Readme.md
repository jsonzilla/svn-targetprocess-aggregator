# SVN TargetProcess Aggregator

This tool has been created to help you to integrate data TargetProcess and SVN repository data, for further analysis.

## Requirements
-   PostgresSQL [download](https://www.postgresql.org/download/)
-   Java SDK 11 [download](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
-   Sbt [download](https://www.scala-sbt.org/download.html)


## Installation and Configuration
### Database
Create a user and database
[link for example](https://www.postgresql.org/docs/8.0/static/sql-createuser.html)

```
CREATE USER "user" WITH PASSWORD 'password';
CREATE DATABASE "database";
GRANT ALL PRIVILEGES ON DATABASE "database" TO "user";
```
### Change database connection (/config/application.conf)
The default connection is a config for running in memory
```conf
slick.dbs.default.profile="slick.jdbc.H2Profile$"
slick.dbs.default.db.driver="org.h2.Driver"
slick.dbs.default.db.url="jdbc:h2:mem:play"
slick.dbs.default.db.user=sa
slick.dbs.default.db.password=""AliveConnection = true
```

Change to use a real database
```conf
slick.dbs.default.profile="slick.jdbc.PostgresProfile$"
slick.dbs.default.db.connectionPool = disabled
slick.dbs.default.db.driver="org.postgresql.Driver"
slick.dbs.default.db.url="jdbc:postgresql:database_name?user=postgres&password=default"
slick.dbs.default.db.keepAliveConnection=true
slick.dbs.default.db.users="postgres"
slick.dbs.default.db.password="default"
slick.dbs.default.db.maxActive=2
slick.dbs.default.db.maxConnections=20
slick.dbs.default.db.numThreads=10Threads = 10
```

### Repository configuration (/config/application.conf)
```conf
repo.user = "your_user"
repo.pass = "pass"
repo.url = "https://svn..."
```

### TargetProcess configuration (/config/application.conf)
For agile flow data aggregation using [TargetProcess] (www.targetprocess.com)
Currently only support basic authentication.
```conf
task_model = {
  patternParser = "(#\\d)\\d+"
  patternSplit = "#"
  separator = ""
}
target = {
  user = "user"
  pass = "pass"
  url = "https://example.tpondemand.com/api/v1"
}
```

#### Configure the task format (/config/application.conf)
Defines how the information about the tasks is extracted
```conf
patternParser = "(#\\d)\\d+" //task or fix #NUMBER
patternSplit = "#" //task or fix #NUMBER
separator = ""
```

### Devel
```bash
> sbt "-Dconfig.file=/... path .../application.conf
$> run
```

### Create structure in database
```
curl --location --request POST 'http://127.0.0.1:9000/boot/YOUR_MAGIC_SECRET' \
--header 'X-API-Key: AbCdEfGhIjK1'
```

### Run the tool for populate the database
```bash
curl --location --request POST 'http://127.0.0.1:9000/api/v1/updateall' \
--header 'X-API-Key: AbCdEfGhIjK1'
```

---

## Check tests
Open console and run the following command
```bash
> sbt test
```

---

## Generate package
Run this on __production__, generate the package to run the application
```bash
> sbt universal:packageBin
```

And copy the generate 'package' from target.
```bash
> ./path_generated/bin/target/universal/stage/bin/svn-targetprocess-aggregator
```
> __Note:__ Remember to open port 9000 on server

After configuration, run the application

---
## Auxiliar scripts
All the scripts are in the folder 'scripts'

First remember to configure the the configuration file (/scripts/configuration.py)
* boot.py - dispatch a command to create the database structure
* configuration.py -  configure this script level variables
* gemerate_reports.py - generate the reports
* last_commits.py - get the last commits from the repository
* update_auto.py - update the database automatically
* LOC/configuration.py -  configure LOC script level variables
* LOC/count_lines_json.py - count the lines of code in the json file

---

## API
This file defines all application routes (Higher priority routes first)
### Authors
* GET /api/v1/authors
* GET /api/v1/authors/:id
* GET /api/v1/authors/bugs/:author - get the bugs of an author

### Commits
* GET /api/v1/commits
* GET /api/v1/commits/:id
* GET /api/v1/commits/revision/:revision
* GET /api/v1/commits/:from/to/:to
* GET /api/v1/commits/custom/:customField/:fromTime/to/:toTime/csvQueryLocalDate
* GET /api/v1/commits/custom/:customField/:fromTime/to/:toTime

### Tasks
* GET /api/v1/tasks
* GET /api/v1/tasks/:id
* GET /api/v1/tasks/task/:taskId - get the task by targetprocess id
* GET /api/v1/tasks/nested/:parentId - get the tasks nested in a task

### Commits tasks
* GET /api/v1/committasks
* GET /api/v1/committasks/:id

### Files by commit
* GET /api/v1/commitfiles
* GET /api/v1/commitfiles/:id

### Files
* GET /api/v1/files
* GET /api/v1/files/bugs - get the files with bugs related
* GET /api/v1/files/:id

### Target process custom fields
* GET /api/v1/customfields
* GET /api/v1/customfields/:id
* GET /api/v1/customfields/field/:field

### Dump
Generate a report like a dump for analysis this data in external tools
* GET /api/v1/dump/:from/to/:to - from commit to commit
* GET /api/v1/dump/:from/to/:to/csv - from commit to commit in csv format
### Endpoints for update
* POST /api/v1/updateall - force update all data
* POST /api/v1/update - only update from the last commit stored in the database
* POST /api/v1/update/custom/:customField - update based on a custom field like task or fix

---

## Database structure
Sql code to create the database structure, in the file
```/scripts/database_structure.sql```.

Overview of the database structure

### API_KEY
Field Name	| Field Type
-------------|-------------
api_key	| Character Varying:I:U
name	| Character Varying
active	| Boolean
id	| BigInt:I:U

### API_LOG
Field Name	| Field Type
----------	| -----------
date	| Timestamp Without Time Zone
ip	| Character Varying
api_key	| Character Varying:N
token	| Character Varying:N
method	| Character Varying
uri	| Character Varying
request_body	| Character Varying:N
response_status	| Integer
response_body	| Character Varying:N
id	| BigInt:I:U


### API_TOKEN
Field Name	| Field Type
-------------|-------------
token	Character | Varying:I:U
api_key	Character | Varying
expiration_time	| Timestamp Without Time Zone
user_id	| BigInt
id	| BigInt:I:U


### AUTHORS
Field Name	| Field Type
-------------|-------------
author | Character Varying:I:U
id	| BigInt:I:U

### COMMITFILES
Field Name |Field Type
-------------|-------------
typeModification	| Integer:N
copyPath_id	| BigInt:N
copyRevision	| BigInt:N
path_id	| BigInt
revision |	BigInt
id	| BigInt:I:U

### COMMITS
Field Name| Field Type
-------------|-------------
message	| Character Varying:N
timestamp	| Timestamp Without Time Zone:N
revision	| BigInt:I:U
author	| BigInt
id	| BigInt:I:U

### COMMITTASKS
Field Name	| Field Type
-------------|-------------
task_id |	BigInt
commit_id |	BigInt
id |	BigInt:I:U

### CUSTOMFIELDS
Field Name |Field Type
-------------|-------------
field_value	| Character Varying:N
field	| Character Varying
task_id	| BigInt:I:U
id |	BigInt:I:U

### FILES
Field Name| Field Type
-------------|-------------
path	| Character Varying:I:U
id |	BigInt:I:U

### TASKS
Field Name |	Field Type
-------------|-------------
type_task	| Character Varying:N
type_task_id |	BigInt:N
user_story	| BigInt:N
time_spend	| Double Precision:N
parent_id |	BigInt:N
task_id	| BigInt:I:U
id	| BigInt:I:U

### USERS
Field Name| Field Type
-------------|-------------
email	| Character Varying:I:U
password | Character Varying
name| Character Varying
emailConfirmed |	Boolean
active |	Boolean
id| BigInt:I:U

