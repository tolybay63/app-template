@echo off

rem �஢��塞 ����稥 MinIO-������
mc.exe -v

rem ����㧪� MinIO-������
if %ERRORLEVEL% neq 0 (
  echo "����㧪� MinIO-������"
  curl -o mc.exe https://dl.min.io/client/mc/release/windows-amd64/mc.exe
  echo MinIO-������ ��⠭�����
) else (
  echo MinIO-������ 㦥 ��⠭�����
)


echo ����ன�� ᮥ�������

mc.exe alias set app-files http://localhost:9000 minioadmin minioadmin
mc.exe alias set app-pages http://localhost:8000 minioadmin minioadmin



echo ""
echo �������� ��������� bucket-��

rem �������� ��� ��ꥪ⮢ ����� bucket
mc.exe rm --recursive --force app-files/files
mc.exe rm --recursive --force app-pages/pages
mc.exe rm --recursive --force app-pages/pages-preview
rem  �������� ᠬ��� bucket
mc.exe rb app-files/files
mc.exe rb app-pages/pages
mc.exe rb app-pages/pages-preview



echo ""
echo �������� bucket-��

mc.exe mb app-files/files
mc.exe mb app-pages/pages
mc.exe mb app-pages/pages-preview



echo ""
echo ���᮪ bucket-��

mc.exe ls app-files
mc.exe ls app-pages

