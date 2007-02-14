@echo off

echo ==================================== Clean ===========================================
call %M2_HOME%\bin\mvn.bat clean
cd website
call %M2_HOME%\bin\mvn.bat clean
cd ..

echo ==================================== Build ===========================================
call build_tck.bat

echo ==================================== Bundle ==========================================
cd main
call build_release.bat
cd ..

echo ==================================== Site ============================================
cd website
call build.bat
cd ..
xcopy /I /E main\target\apidocs website\target\xsite\apidocs

echo ==================================== Test ============================================
cd target
java -jar objenesis-tck-*.jar
cd ..
