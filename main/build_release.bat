@echo off
%M2_HOME%\bin\mvn.bat source:jar javadoc:jar repository:bundle-create
