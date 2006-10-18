@for /R src %%a in (*.java) do  cat %%a >>all
@for /R src %%a in (*.java) do  echo %%a >>list