@echo off

%M2_HOME%\bin\mvn.bat clean resources:copy-resources xsite:run
