$version=Read-Host '请输入新版本号(eg. 0.0.1)'
(Get-Content './build.gradle')  -replace 'version = .*',('version = '''+$version+'''') | Set-Content './build.gradle'
(Get-Content './src/main/resources/mcmod.info')  -replace ' "version":.*?,',(' "version": "'+$version+'",') | Set-Content './src/main/resources/mcmod.info'
(Get-Content './src/main/java/cn/catlemon/aol_core/AoLCore.java')  -replace '    public static final String VERSION = .*?;',('    public static final String VERSION = "'+$version+'";') | Set-Content './src/main/java/cn/catlemon/aol_core/AoLCore.java'
(Get-Content './src/main/java/cn/catlemon/aol_core/api/package-info.java')  -replace '@net\.minecraftforge\.fml\.common\.API\(apiVersion = ".*?", owner = cn\.catlemon\.aol_core\.AoLCore\.MODID, provides = "AoLCoreAPI"\)',('@net.minecraftforge.fml.common.API(apiVersion = "'+$version+'", owner = cn.catlemon.aol_core.AoLCore.MODID, provides = "AoLCoreAPI")') | Set-Content './src/main/java/cn/catlemon/aol_core/api/package-info.java'
Write-Host '修改成功'
pause