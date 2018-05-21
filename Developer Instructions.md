Developer Instructions  
===================  

Usage notes and limitations  
------------------  

* `File Injector` and `Music Swapper` are intended to be developer tools and not for general users:
    * They can only modify either an index1 or index2 file, and cannot modify them simultaneously
    * File Explorer will automatically back up the index, but cannot handle game patches beyond a certain size
    * `File Injector` cannot open indexes beyond a certain size and thus cannot modify them
    * `File Injector` and `Music Swapper` cannot automate the process of installing a file, which requires 
       the user to confirm they are targeting the correct file
* Since `File Injector` and `Music Swapper` cannot modify both indexes at the same time, 
  it may be possible for them to get out of sync
    * Being out of sync means that the game may refer to both the newly modified file, and the original version at different times depending on which index is being used at the time
* Patching the game with modifications installed may result in corruption if the same file is also patched while being modified


Injecting a new file
------------------  

1. Open the game volume in the main File Explorer window, navigate to the file in question, and note the hash and offset URIs
2. Extract the file with `File -> Save File` and modify it as desired
3. Go to `Tools -> File Injector` and open the game volume again.
4. A temporary backup will be made of the affected index file that will be modified
5. Find the file again on the `Original Id` list, and confirm that the offset matches the original file
6. Add the new file by selecting `Add` and navigating to the file
7. Select `Generate Dat` to create a new sub volume with the file in it
8. Confirm that the file is selected on the `Set to:` list and that it lists the file path of the file just added to the `Custom Files` list from step 6
9. Once both the original target file in `Original Id` and the newly injected file in `Set to` are correct, select  `Set` to enable the file
10. Test the file
11. Once the file is working, prepare it for distribution with a suitable tool
12. Restore the game by deleting the modified index and renaming the backup
13. Delete the newly created sub volume (it should be the smallest/newest dat container)