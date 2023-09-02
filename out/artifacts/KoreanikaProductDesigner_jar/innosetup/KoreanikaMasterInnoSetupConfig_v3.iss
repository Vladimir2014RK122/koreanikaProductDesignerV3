; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "KoreanikaMDesigner v3"
#define MyAppVersion "3.0"
#define MyAppPublisher "KoreanikaMv3"
#define MyAppURL "https://www.koreanika.ru/"
#define MyAppExeName "koreanika.exe"
#define MyAppCurrentUpdateVersion "3.0.49"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
;SignTool=MsSign $f
AppId={{95EB5EB1-6F16-4363-A6B7-141A62BF1C97}
AppName={#MyAppName}
AppVersion={#MyAppCurrentUpdateVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}

;c:\Program files
;DefaultDirName={autopf}\{#MyAppName}        
;c:\MyAPP
usePreviousAppDir=no
DefaultDirName={sd}\{#MyAppName}              

AlwaysShowDirOnReadyPage=no

DisableProgramGroupPage=yes
; Uncomment the following line to run in non administrative install mode (install for current user only.)
;PrivilegesRequired=lowest
OutputDir=C:\Users\06718\YandexDisk\Programming\ComercialProjects\KoreanikaProductDesigner\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\innosetup
OutputBaseFilename=KoreanikaMSetup {#MyAppCurrentUpdateVersion}
SetupIconFile=C:\Users\06718\YandexDisk\Programming\ComercialProjects\KoreanikaProductDesigner\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\exeWrapper\forKPDBuild\koreanika_icon_2.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=admin
UsePreviousPrivileges=no
UninstallFilesDir={app}\uninst

ChangesAssociations = yes
ShowLanguageDialog=no

[Languages]
Name: "ru"; MessagesFile: "compiler:Languages\Russian.isl"

[UninstallDelete]
Type: filesandordirs; Name: "{app}\jre"
Type: filesandordirs; Name: "{app}\features_resources"
Type: filesandordirs; Name: "{app}\materials_resources"

[Registry]
Root: HKCR; Subkey: ".kproj";                             ValueData: "{#MyAppName}";          Flags: uninsdeletevalue; ValueType: string;  ValueName: ""
Root: HKCR; Subkey: "{#MyAppName}";                     ValueData: "Program {#MyAppName}";  Flags: uninsdeletekey;   ValueType: string;  ValueName: ""
Root: HKCR; Subkey: "{#MyAppName}\DefaultIcon";             ValueData: "{app}\{#MyAppExeName},0";               ValueType: string;  ValueName: ""
Root: HKCR; Subkey: "{#MyAppName}\shell\open\command";  ValueData: """{app}\{#MyAppExeName}"" ""%1""";  ValueType: string;  ValueName: ""

[Code]

{ ///////////////////////////////////////////////////////////////////// }
function GetUninstallString(): String;
var
  sUnInstPath: String;
  sUnInstallString: String;
begin
  sUnInstPath := ExpandConstant('Software\Microsoft\Windows\CurrentVersion\Uninstall\{#emit SetupSetting("AppId")}_is1');
  sUnInstallString := '';
  if not RegQueryStringValue(HKLM, sUnInstPath, 'UninstallString', sUnInstallString) then
    RegQueryStringValue(HKCU, sUnInstPath, 'UninstallString', sUnInstallString);
  Result := sUnInstallString;
end;


{ ///////////////////////////////////////////////////////////////////// }
function IsUpgrade(): Boolean;
begin
  Result := (GetUninstallString() <> '');
end;


{ ///////////////////////////////////////////////////////////////////// }
function UnInstallOldVersion(): Integer;
var
  sUnInstallString: String;
  iResultCode: Integer;
begin
{ Return Values: }
{ 1 - uninstall string is empty }
{ 2 - error executing the UnInstallString }
{ 3 - successfully executed the UnInstallString }

  { default return value }
  Result := 0;

  { get the uninstall string of the old app }
  sUnInstallString := GetUninstallString();
  if sUnInstallString <> '' then begin
    sUnInstallString := RemoveQuotes(sUnInstallString);
    if Exec(sUnInstallString, '/SILENT /NORESTART /SUPPRESSMSGBOXES','', SW_HIDE, ewWaitUntilTerminated, iResultCode) then
      Result := 3
    else
      Result := 2;
  end else
    Result := 1;
end;

{ ///////////////////////////////////////////////////////////////////// }
procedure CurStepChanged(CurStep: TSetupStep);
begin
  if (CurStep=ssInstall) then
  begin
    if (IsUpgrade()) then
    begin
      UnInstallOldVersion();
      //SaveStringToFile(ExpandConstant('{app}\version'), '{#MyAppCurrentUpdateVersion}' , False);
    end;
  end;

  if(CurStep=ssPostInstall ) then  
  begin
    SaveStringToFile(ExpandConstant('{app}\version'), '{#MyAppCurrentUpdateVersion}' , False);
  end;
end;

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\06718\YandexDisk\Programming\ComercialProjects\KoreanikaProductDesigner\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\filesForInnoSetupCommon\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\06718\YandexDisk\Programming\ComercialProjects\KoreanikaProductDesigner\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\filesForInnoSetupCommon\*"; Excludes: "updater.properties, version"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\06718\YandexDisk\Programming\ComercialProjects\KoreanikaProductDesigner\KoreanikaProductDesigner\out\artifacts\KoreanikaProductDesigner_jar\filesForInnoSetupKoreanikaMaster\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
;Filename: "{app}\{#MyAppExeName}"; Flags: runascurrentuser;
