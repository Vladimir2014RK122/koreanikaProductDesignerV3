; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "KoreanikaDesigner"
#define MyAppVersion "1.0"
#define MyAppPublisher "Koreanika"
#define MyAppURL "https://www.koreanika.ru/"
#define MyAppExeName "koreanika.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{1357348A-37A3-4785-B271-FA0411963CD2}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DisableProgramGroupPage=yes
; Uncomment the following line to run in non administrative install mode (install for current user only.)
;PrivilegesRequired=lowest
OutputDir=C:\Users\VladimirMac\YandexDisk\Programming\������� �������\����������� ��� ���������\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\innosetup
OutputBaseFilename=KoreanikaSetup
SetupIconFile=C:\Users\VladimirMac\Desktop\koreanika_icon_2.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\VladimirMac\YandexDisk\Programming\������� �������\����������� ��� ���������\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\files for innoSetup\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\VladimirMac\YandexDisk\Programming\������� �������\����������� ��� ���������\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\files for innoSetup\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
