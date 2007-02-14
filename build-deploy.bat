svn up
call setenv.bat
call %NB_ANT% rebuild-release

call ..\MS\bombus.bat
pause

call %NB_ANT% deploy-release

