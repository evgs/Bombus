svn up
call setenv.bat
%NB_ANT% rebuild-release

cd ..\MS\
call bombus.bat
cd ..\Bombus\

%NB_ANT% deploy-release

