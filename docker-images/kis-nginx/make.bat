rem ����ਡ�⨢� � ᥡ� (���� ᡮઠ �㣠����, �� 䠩�� ᭠�㦨 �����)
rmdir /s /q  data\html

mkdir data\html
mkdir data\error-page

rem
xcopy /e /i /y ..\..\quasar-project\dist\spa\. data\html\

rem
xcopy /e /i /y ..\..\kis-web\public\error-page\. data\error-page\


rem ����ࠥ� ��ࠧ Docker
docker build -f Dockerfile.win -t kis-nginx .

