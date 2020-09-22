$version=Read-Host '请输入新版本号(eg. 0.0.1)'
(Get-Content './build.gradle')  -replace '"Specification-Version": .*?,',('"Specification-Version": "'+$version+'",') | Set-Content './build.gradle'
(Get-Content './src/main/resources/mcmod.info')  -replace ' "version": .*?,',(' "version":"'+$version+'",') | Set-Content './src/main/resources/mcmod.info'
(Get-Content './src/main/java/cn/catlemon/aol_core/aol_core.java')  -replace '    public static final String VERSION = .*?;',('    public static final String VERSION = "'+$version+'";') | Set-Content './src/main/java/cn/catlemon/aol_core/aol_core.java'
Write-Host '修改成功'
pause