svn up
call setenv.bat
call %NB_ANT% rebuild-jzlib-release
@if ERRORLEVEL 1 echo ==============BUILD FAILED=================
