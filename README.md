# Poker24
## Description
24 poker game using distributed computing technology in Java

## How to Play
At least two players are present.
Cards of A,1,2...K are numbered as 1 to 13.
Parse the formula to make up a sum of 24.
If correct, players are then notified who is the winner and move to the next round.

The first one to guess the formula of 
## Principle
* Use connection factory, queue and topic provided by Glassfish to simulate the server system.
* Use JDBC to connect with SQLite database
* Use SQL statements to calculate ranks
* Use RMI to send remote procedure calls
* A timer principle
  * Wait for 10 seconds for players to join.
  * If only 1 player is found after 10 seconds, wait until one more player joins and the game starts immediately.
  * If there are 4 players, ignore the timer and the game starts immediately.

## Setup
1. Install Java JDK.
2. Pull the server files from [here](https://github.com/manchunw/Poker24Server).
3. Download and Install Glassfish
4. Pull the client files from this repository.
5. Open Commpand Prompt and start Glassfish domain as follows.
```
cd "C:\<glassfish directory>\bin"
asadmin start-domain
```
6. Open a web browser and enter `http://localhost:4848`
7. Create a connection factory.
  * Name: jms/JPoker24GameConnectionFactory
  * Type: javax.jms.ConnectionFactory
8. Create two destination resources.
  * Queue
    * Name: jms/JPoker24GameQueue
    * Type: javax.jms.Queue
  * Topic
    * Name: jms/JPoker24GameTopic
    * Type: javax.jms.Topic
9. Create a file called `security.policy` in the client and server folder and enter the following.
```
grant {
permission java.net.SocketPermission "*:1024-65535", "connect,accept";
permission java.net.SocketPermission "*:80", "connect";
permission java.util.PropertyPermission "*", "read,write";
permission java.io.FilePermission "jpoker24game.sqlite", "read,write";
permission java.lang.RuntimePermission "loadLibrary.*";
permission java.io.FilePermission ".", "read";
permission java.io.FilePermission "-", "read";
permission java.io.FilePermission "${java.io.tmpdir}/-", "read,write,delete";
permission java.lang.RuntimePermission "accessDeclaredMembers";
permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
permission java.lang.RuntimePermission "setContextClassLoader";
permission java.lang.RuntimePermission "modifyThread";
permission java.io.FilePermission "lib\gf-client.jar", "read";
};
```
10. Setup is done!

## How to use
1. Open Commpand Prompt and start Glassfish domain.
  ```
  cd "C:\<glassfish directory>\bin"
  asadmin start-domain
  ```
2. Open another Command Prompt and start RMI registry.
  ```
  cd "C:\<JDK directory>\bin"
  start rmiregistry
  ```
3. Open another Command Prompt and start the server.
  ```
  java -Djava.security.policy="C:\<Poker24 directory>\security.policy" -cp "C:\<glassfish directory>\lib\gf-client.jar;C:\<Poker24 directory>\JPoker24GameServer.jar;C:\<Poker24 directory>\sqlite-jdbc-3.8.7.jar" JPoker24GameServer jpoker24game.sqlite
  ```
4. Open another Command Prompt and start the client.
  ```
  java -Djava.security.policy="C:\<Poker24 directory>\security.policy" -cp "C:\<glassfish directory>\lib\gf-client.jar;C:\<Poker24 directory>\JPoker24GameServer.jar;C:\<Poker24 directory>\sqlite-jdbc-3.8.7.jar" JPoker24GameServer jpoker24game.sqlite
  ```
5. Done! You can play the game.
