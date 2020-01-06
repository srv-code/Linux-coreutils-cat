# List 
## Synopsis
A utility program to concatenate files and prints on the standard output.

## Features
- Option to enable verbose mode i.e. shows additional (e.g. file name, total file count, line counts, character counts etc.) information for each files being processed.
- Option to show the line numbers while printing each line of each file.
- Option to show the character count of each file being printed.
- Option to show the file contents in five different representational notations: binary, octal, decimal, hexadecimal.

### Default behavior
- If no file is specified then treats the standard input stream as the file.
- Verbose mode is disabled.
- Line numbers, file names, total file count, character counts, line counts are not shown.
- File contents are shown in the unicode character mode.