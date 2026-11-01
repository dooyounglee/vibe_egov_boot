# Apply the project JDK to the current PowerShell session.  usage: . .\env.ps1
$jdk = (Get-Content -Path (Join-Path $PSScriptRoot '.mvn\jdk-home') -TotalCount 1).Trim()
$env:JAVA_HOME = $jdk
$env:Path = "$jdk\bin;$env:Path"
Write-Host "JAVA_HOME=$jdk"
