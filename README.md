# Devi Bot
Devi is a Discord chat and music bot.

## Booting Devi on a Winodws VPS / PC

### Open Command Line

You can either use Windows' Command Prompt bye typing 'cmd' into the search field or Git Bash by right clicking and selecting 'Git Bash Here'.

### Clone the repository

Clone the repository by entering the following command into your command line. The temporary directory will be deleted compiling the jar file.  

```
git clone https://github.com/RealPurox/DeviBot.git temp
```

You might have to login into your GitHub account when you try to clone this repository.

### Change current directory

Change command line directory to temp as we called the directory temp when cloning the repository.

```
cd temp
```

### Compile Jar

Compile the current master version of Devi.

```
mvn clean compile assembly:single
```

### Move Jar

Move the jar to the desktop so we can get rid of the temporarily created directory.

```
mv target/Devi[PRESS TAB FOR AUTOCOMPLETE] ~/Desktop
```

Because the .jar name is pretty long and you don't want to type it out every time, you can just type "Devi" and then press tab. The command line should autocomplete the jar file name. The command you're executing should look like this, if you've done everything right so far

```
mv target/Devi-1.0.0-ad-beta-jar-with-dependencies.jar ~/Desktop
```

### Change current directory

Change command line directory to the Desktop so we can delete the temporary directory.

```
cd ~/Desktop
```

### Delete temp directory

Delete the temp directory as we don't need it anymore.


```
rm -rf temp
```

### Execute jar

You should be able to execute the jar file now. Below is an example on how to run it.

##### REMEMBER: If you don't add --devi at the end ofthe command line, the dev bot token will be used.

```
java -jar -Xmx[MAX AMOUNT OF OF MG TO USE] Devi[PRESS TAB FOR AUTO COMPLETE] --devi
```
##### Example:

```
java -jar -Xmx2047M Devi-1.0.0-ad-beta-jar-with-dependencies.jar --devi
```
