# DuplicateFinder

DuplicateFinder is a tool to find files duplicated across multiple directories.

## Motivation

The main motivation for this tool was to cleanup photo collections.

## Solution

A command line tool that will compute a hash of all the files and then check if multiple hashes match.
The hashes are stored in a LevelDB database.  The tool supports the following commands:
* cleanup: removes hashes from the database
* display: list the files that match a pattern
* duplicate: scans the database to detect duplicate files
* hash: surprisingly, hashs the given directories and add the results to the database

Files that ends with: lnk, IBO, IFO, txt, ini are never hashed.

## Todo

* Expose the comparison algorithms
* Expose what filename extensions to ignore

# Examples

There is a detailed usage displayed when the tool is called without any argument, use it !

## Hashing

	java -jar DuplicateFinder.jar  hash 
		--database C:\img.db 
		C:\photos-boulot C:\photos-perso C:\photo-dodo

This example will:
* open the database 'img.db'
* hash the contents of the directories: C:\photos-metro C:\photos-boulot C:\photo-dodo

## Duplicate

	java -jar DuplicateFinder.jar  duplicate --database C:\img.db

This example will:
* open the database 'img.db'
* compute the duplicates for it and output the result in the file duplicates.txt
