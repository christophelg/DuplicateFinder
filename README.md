# DuplicateFinder

DuplicateFinder is a tool to find files duplicated across multiple directories.

# Motivation

The main motivation for this tool was too cleanup my personal photo collections.

# Solution

TODO

# Examples

There is a detailed usage displayed when the tool is called without any argument, use it !

	java -jar DuplicateFinder.jar  hash --database C:\img.db C:\photos-boulot C:\photos-perso C:\photo-dodo

This example will:
* open the database 'img.db'
* hash the contents of the directories: C:\photos-metro C:\photos-boulot C:\photo-dodo

	java -jar DuplicateFinder.jar  duplicate --database C:\img.db

This example will:
* open the database 'img.db'
* compute the duplicates for it and output the result in the file duplicates.txt

# Todo

* Expose the comparison algorithms
* Expose what filename extensions to ignore
