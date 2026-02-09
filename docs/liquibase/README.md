# [![Liquibase](https://www.liquibase.org/custom_images/home_tagline.png "liquibase.org")](https://www.liquibase.org/)

Liquibase is used by our projects to apply database changes in a repeatable manner. Liquibase keeps track of which scripts have been run, and runs the scripts which haven't been run yet in the correct order.

## Table of Contents
- [Writing Scripts](#write)
- [Running Scripts](#run)
- [Best Practices](#best)
- [Troubleshooting](#trouble)
- [Rollback Liquibase Changes](#rollback)

<a name="write"></a>
# Writing Scripts
- All changes to the database should be made via Liquibase scripts.
  - (Exception: Dummy data should be added directly to the database, since it shouldn't go to production.)
- Scripts go in `api\src\main\resources\database`.
- SQL Scripts must have special comments at the beginning of the file
  ```
  --liquibase formatted sql
  --changeset author:ScriptName endDelimiter:go
  ```
    Replacing `author` with your name and `ScriptName` with a unique name for the script (e.g. `--changeset sbeller:AddFooTable endDelimiter:go`) 


<a name="run"></a>
# Running Scripts
- **All SQL scripts should be run through liquibase!**
- Run the following command from the api folder:
> mvn liquibase:update -PLOCAL
<a name="best"></a>
# Best Practices
- Liquibase scripts will be run on application startup in production. Liquibase is configured to run everything under `src/main/resources/database/changelogs` in alphabetical order. As such it's important to name your files with the pattern `yyyymmdd.xx - DESCRIPTION.xml` (e.g. `20180516.01 - Add ACTIVE_FLG to USERS.xml`) to ensure scripts are run in the same order they were created in.
- Avoid changing scripts that already ran. Whenever possible, make corrections and additions with a new script. Liquibase should be thought of as a replay of all the changes that were done in development.


<a name="trouble"></a>
# Troubleshooting 
- If liquibase fails, identify which script is failing and contact its author.
- Sometimes you NEED to update a file that has already been run. Ideally, you should restore the database from a recent backup. If that isn't an option, you can edit the file, run it manually, and then run the following command to clear the liquibase checksums and the 
> mvn liquibase:clearCheckSums -PLOCAL

<a name="rollback"></a>
# Rollback Liquibase Changes
You may rollback Liquibase Changelogs using the following command:

`mvn liquibase:rollback -Dliquibase.rollbackCount=1 -PLOCAL -Ddatasource.password=[PASSWORD]`

> **TIP:** Only a few operations have automatic rollback support, generally you have to [include rollback information in your Liquibase script](http://www.liquibase.org/documentation/rollback.html)

