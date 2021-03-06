FFXIV File Explorer  
===================  

A file browser that can parse and display multiple FFXIV file types and optionally swap them for user made versions.  



Requirements  
===================  

##  System requirements:  

* OS List:

    | OS Name                                        | Status    |  
    |------------------------------------------------|----------:|  
    | Windows 7 or newer                             | Supported |  
    | MacOS                                          |  Untested |  

    * Some features are platform dependant:  
        * Lua script parsing is only available on Windows x64  

* Software requirements  
    * Java 8 Runtime  


## Game requirements:  
### Platform and Languages:  

| Platform Name                    | Status        | \| | Language Name           | Status    |  
|----------------------------------|--------------:|----|-------------------------|----------:|  
| Windows x86/x64 Client Steam     |     Supported | \| | Worldwide (JP/EN/FR/DE) | Supported |  
| Windows x86/x64 Client Non-Steam |     Supported | \| | China (CN)              | Supported |  
| MacOS x86 Client Non-Steam       |     Supported | \| | Korea (KR)              | Supported |  
| PlayStation 4                    | Not supported |  

### Versions:  

| Expansion Name                     | Status          | \| | Benchmark Name                                       | Status          |  
|------------------------------------|----------------:|----|------------------------------------------------------|----------------:|  
| Final Fantasy XIV (1.0)            |   Not supported | \| | Final Fantasy XIV Benchmark (1.0)                    |   Not supported |  
| A Realm Reborn Alpha/Beta (2.0α/β) |       Supported | \| | A Realm Reborn Benchmark (Character Creation) (2.0α) |       Supported |  
| A Realm Reborn (2.0)               |       Supported | \| | A Realm Reborn Benchmark (Exploration) (2.0β/2.0)    |       Supported |  
| Heavensward (3.0)                  | Limited support | \| | Heavensward Benchmark (3.0)                          | Limited support |  
| Stormblood (4.0)                   | Limited support | \| | Stormblood Benchmark (4.0)                           | Limited support |  

> Working with index2 results in no file paths being available to the user, all entries are hashes


Building from source
===================  

* Software Requirements
    * A suitable JDK
        * OpenJDK 1.8-RedHat
            > OpenJDK 10-RedHat is **NOT** compatible and will produce build errors
        * Java SE JDK 1.8
    * An active internet connection (When compiling with Maven/VS Code)

* Supported Projects:
    * Apache Maven 3.5
    * Microsoft Visual Studio Code 1.23
        * VS Code requires Maven to compile
    * IntelliJ IDEA 2018.1.3

    > While there are Eclipse project files, they are exclusively for the use of VS code, Eclipse itself is **NOT** supported

* How to build
    * Clone or download the source code
        * Make sure that there are no spaces in the path to it
    * Install a suitable JDK and confirm that the `JAVA_HOME` environmental variable is set to point to it
    * For Maven:
        * Make sure Maven is set in your `PATH` environmental variable
        * Run `mvn -f <path to source code>\pom.xml package`
        * Output is in `<path to source code>\target`
    * For VS Code:
        * Make sure Maven is set in your `PATH` environmental variable
        * Open the folder
        * Go to `Tasks` → `Run Build Task`
        * Output is in `<path to source code>\target`
    * For IDEA
        * Open the folder
        * Download the missing dependencies:
            * Java SQLite-jdbc 3.21.0.1
            * JCraft JZlib 1.1.3
            * JOAMP JOGL 2.3.2/b1473 (and all subpackages)
            * JOAMP Gluegen 2.3.2/b904 (and all subpackages)
            * Google Gson 2.8.4
            * Dheid Colorpicker 1.1
        * Add dependency JARs to `<path to source code>\libs`
            > Place the JARs directly in `libs`, do not include folders
        * Go to `Build` → `Build Artifacts`
        * Output is in `<path to source code>\out`
            * Manually copy contents of `<path to source code>\resources` into `<path to source code>\out`
