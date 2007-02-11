svn up
call setenv.bat
%NB_ANT% rebuild-release
call ..\MS\bombus.bat
%NB_ANT% deploy-release

