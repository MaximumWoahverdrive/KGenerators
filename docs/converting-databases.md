# Converting databases

### Introduction

KGenerators allows you to store data in a various database types. In case you want to migrate data between them you should use this tutorial.

### Converting databases step by step

1. For safety reasons you should make backup of all your KGenerators data
2. Stay on database you are currently using
3. If you are converting to MySQL - configure it in config.yml
4. Open server console, as migrating data is only possible from there
5. Choose your new database type (MYSQL/SQLITE)
6. Type command `kgenerators convertdbto <database_type>`
7. Wait a while, migration will take place, database type in config.yml should be updated automatically

In case of any problem occurred try to restart server and repeat previous steps. If issue still persists create `/kgen debug` link and contact us.
