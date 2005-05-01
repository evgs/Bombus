echo on
call moto.bat
del bombus*.rar
rar32 a -s -agmm_dd bombus @list_all.txt