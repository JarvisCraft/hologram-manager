# HologramManager [Minecraft Spigot]
A ProtocolLib based Minecraft-Spigot API for developers to implement their own simple and yet advanced holograms.

## Maven:
Due to this library using custom repositories it cannot be deployed to Maven Central so in order to use it you can use QuestCraft public repo:
```xml
<repositories>
  ...
  <repository>
      <id>questmg-public</id>
      <url>http://dev.questmg.ru:8081/artifactory/public/</url>
  </repository>
  ...
</repositories>
```
```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.github.JarvisCraft</groupId>
    <artifactId>spigot-HologramManager</artifactId>
    <version>-SNAPSHOT</version>
  </dependency>
  ...
<dependencies>
```
