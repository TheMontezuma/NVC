# NVC
A tool for organizing files on SD cards for SIO2SD, etc.

Working with a huge amount of files can be problematic even with a fast PC. You can help yourself by setting a directory structure. A similar problem we have when we want to put ATARI games, demos and applications on an SD card and later search with SIO2SD for a specific title (either with keys or with the Configurator). This is especially useful if there are more than 12 thousands titles on the card (so many games you will find in the archive available at http://www.atarionline.pl). Last but not least - SIO2SD does not sort files alphabetically (they are displayed in the order they were copied).

Since the Version 3.0 of the SIO2SD Software, the Configurator supports file searching. If you know a file name (or its part), you can enter the search mask and the device will search the complete SD card and present you the results. Regardless if you use the search feature or not, it is still a good idea to organize your files in directories. NVC will do the job for you.

About the tool

NVC stands for "Next Valid Character". It is a feature of car navigation systems to speed up entering destination address. When you want to enter address, the system shows you a virtual keyboard, where only these letters are selectable, which match entries from the database. If you travel to London, then after entering the first 'L' letter, it will be greyed out, because there is no city which name starts with "Ll". But there will be other letters selectable: 'a' (Lancaster), 'e' (Leeds), 'o' (London), etc.
NVC takes advantage of a similar idea. Its goal is to copy files to the SD card while creating directory structure at the same time. This structure can be easily illustrated through an example:

While copying the MOON PATROL (V1).XEX file, the tool will create directories M/MO/MOO, and copy the file to the MOO directory. The depth of nesting (whether M/MO is fine, or maybe M/MO/MOO/MOON would be better, etc.) is configurable.

If an SD card is empty and we are going to use NVC to populate it with games, then we get an additional bonus:
- NVC will copy files in alphabetical order, so SIO2SD will display them accordingly
- NVC will create a list of all copied files (in the content.txt file, which will be automatically created) 

The NVC tool can be used as well to search for files that got lost somewhere. It is enough to specify the entire drive as an input directory, and NVC will find all files with given suffixes and copy them to the output directory.

NVC is written in JAVA. In order to run it, you will need a JAVA virtual machine.
An installation is not required. After unzipping NVC.zip you will get:

    nvc.jar - the NVC tool
    nvc.txt - a short description (in english)
    nvc.bat - an example script for starting NVC from a command line under Windows (has to be adapted)
    nvc.sh - an example script for starting NVC from a terminal window under Linux (has to be adapted) 

NVC Usage

Double click the nvc.jar file to start the tool. A new window will appear.

If started first time, the tool shows default settings, otherwise it shows the last used settings.

Settings description:

    Input directory - this is where we are copying from - it can be any directory, or even an entire drive (for example C:\)

    Output directory - the destination - it should be the "ATARI" directory on the SD card

    Max output directory structure depth - this value has an influence on the number of directories created in the destination path for a given file. The bigger it is, the smaller is the number of files in the last directory, but also the path that we need to follow to a file is longer. The default value is 3. For bigger game collections you may try higher numbers, and for small collections you may try a value of 2.

    Force directory creation at the specified depth - default is off. If we have a file with a unique beginning of the name (there is no other file, which name starts with the same letters), then we don't have to create directories for it. SIO2SD does not sort files and does not display directories at the front of the files (like PCs do). You may want to enable it if you use NVC to organize your games for usage with a PC (for example with AspeQt or with Atari800 Emulator).

    Suffix list - NVC will copy only the files with suffixes specified here (lowercase / uppercase does not matter) 

Upon clicking on the Start button, the tool searches the Input directory (and all its subdirectories) for files matching given suffixes. It maintains a list containing search results, which is sorted alphabetically after the search is done. NVC follows that list to copy files to the Output directory.

While copying files, NVC creates subdirectories according to the "Max output directory structure depth" and "Force directory creation at the specified depth" settings. If there are files with identical names found, their content will be compared. Identical content means only one copy in the Output directory. If the files are different, all will be copied and the name of every file that follows will be extended with NVCVERX, where X is a counter.

NVC Usage from a command line

NVC can be started alternatively from a command line.

java -jar nvc.jar -i INPUT -o OUTPUT [-f -l LEVEL] suffix1 [suffix2 ... suffixn]
 -f        : force directory creation for files with unique names, default: false
 -i INPUT  : input directory
 -l DEPTH  : output directory depth, default: 3
 -o OUTPUT : output directory
 suffix    : list of suffixes, example: atr com xex xfd

Examples:

java -jar nvc.jar

performs the same action as double-clicking the nvc.jar (you may need it if double-clicking the nvc.jar does not work in your java environment)

java -jar nvc.jar -i input_dir -o output_dir -l 2 atr

search "input_dir" and its subdirectories for files matching "atr" suffix and copy them to "output_dir", while creating a directory structure with up to 2 levels of subdirectories

java -jar nvc.jar -i C:\ -o d:\ATARI -f -l 3 atr xfd xex

search the complete "C" drive for files with "atr", "xfd" and "xex" suffixes and copy them to the "ATARI" directory on the "D" drive, while creating a directory structure with exactly 3 levels of subdirectories

Remarks

    copying files takes time - a progress bar will tell you how many files (in percent) are already copied
    it makes no sense to specify the same directory as an input and as an output directory - you would end up in a mess, since the input files are not being deleted
    the output directory "ATARI" (on the SD card) does not need to be completely empty - if you create directories there before starting NVC, then SIO2SD will show these directories at the top of the directory list (directories created later will be shown at the bottom of that list)
    there are some files (for example "atr" images containing multiple games), which do not fit to the NVC concept, it makes sense to copy such files manually to a separate directory called for example "Collections"

    you will find NVC source code in the nvc.jar file
    
