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

mc.exe alias set kis-files http://localhost:9000 minioadmin minioadmin
mc.exe alias set kis-pages http://localhost:8000 minioadmin minioadmin



echo ""
echo �������� ��������� bucket-��

rem �������� ��� ��ꥪ⮢ ����� bucket
mc.exe rm --recursive --force kis-files/files
mc.exe rm --recursive --force kis-pages/pages
mc.exe rm --recursive --force kis-pages/pages-preview
rem  �������� ᠬ��� bucket
mc.exe rb kis-files/files
mc.exe rb kis-pages/pages
mc.exe rb kis-pages/pages-preview



echo ""
echo �������� bucket-��

mc.exe mb kis-files/files
mc.exe mb kis-pages/pages
mc.exe mb kis-pages/pages-preview



echo ""
echo ���᮪ bucket-��

mc.exe ls kis-files
mc.exe ls kis-pages

