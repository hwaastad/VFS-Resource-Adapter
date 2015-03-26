# VFS-Resource-Adapter
VFS Inbound Resourceadapter

This project is a sample on using a commons vfs2 filemonitor in an inbound resource adapter.

All being available in TomEE web project

# Info
* Configure sftp details in MDB's in web module
* Build parent pom
* mvn tomee:run in web module
 
# MDB configuration
* hostName
* username
* password
* path - which is relative to the users sftp root


This project is still WIP and several options should be configured at resourceadapter level (polling interval etc)
