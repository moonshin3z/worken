@ECHO OFF
set MVNW_CMD=mvn
where %MVNW_CMD% >NUL 2>NUL
IF ERRORLEVEL 1 (
  ECHO Error: Maven no esta instalado. Instalala o ajusta MVNW_CMD en backend/mvnw.cmd.
  EXIT /B 1
)
%MVNW_CMD% %*
