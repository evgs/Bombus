if not exist ftp\midp1.0\Bombus.jad goto exit
call pack_release.bat
ftp -n -s:ftppub
:exit
del ftp\midp1.0\Bombus.jad