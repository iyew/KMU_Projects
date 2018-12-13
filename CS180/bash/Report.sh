#!/bin/bash

#*******************************************************************************
#FileName: Report.sh
#author:   taehee.gweon
#e-mail:   kth658@gmail.com
#Course:   CS180
#date:     2016/11/22
#
#Brief:
#Assignment #2
#The script look for files inside every subdirectory of current directory.
#then, count the number of lines in that file that contain at least
#one occurrence of the specified string.
#Usage ./Report.sh STRING
#*******************************************************************************

#Print script usage message
PrintHelp()
{
  echo "Usage: Report.sh STRING"
  echo ""
  echo "--Finds the occurrences of STRING in the files within the child"
  echo "directories of the current directory, and outputs a file with the"
  echo "occurrence statistics."
  echo ""
  echo "Output: 'Report.txt'"
}

#If the argument is not specified, or if more than one argument is given, execute print message
if [ $# != 1 ]
then
  PrintHelp
  exit 1
fi

#If a single argument of --help is given, execute print message
if [ $1 == "--help" ]
then
  PrintHelp
  exit 0
fi

#Initialize variables
STRING=$1
COUNT=0
DIRTOTAL=0
TOTAL=0

echo "Counting Report for String '$STRING'"
echo ""

#Loop for checking directories
for DIRNAME in Folder?
do
  echo "Directory: $DIRNAME"
  cd $DIRNAME  #Enter the subdirectory

  #Loop for checking .txt Files
  for FILENAME in File?.txt
  do
    let COUNT=$(grep -c $STRING $FILENAME)
    echo "  $FILENAME: $COUNT"
    let DIRTOTAL+=$COUNT
  done

  let TOTAL+=$DIRTOTAL
  echo "Directory Total: $DIRTOTAL"
  DIRTOTAL=0
  echo ""
  cd ..  #Exit the subdirectory
done

echo "Total: $TOTAL"
exit 0
