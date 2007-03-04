svn up
call setenv.bat
call %NB_ANT% rebuild-release

call ..\MS\bombus.bat

call %NB_ANT% deploy-release

