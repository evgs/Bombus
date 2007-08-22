svn up
call setenv.bat
call %NB_ANT% rebuild-release
@if ERRORLEVEL 1 echo ==============BUILD FAILED=================
