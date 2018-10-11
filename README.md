# OBDII_Code_Finder
OBDII Code Finder is quite a useful tool when you're trying to find some information and don't know where to start. Currently I have a database with both the code numbers and the code titles. Obviously this isn't enough to figure out whats going on with you're vehicle. This program also searches through a website with a description of the problem, possible symptoms, if the severity is available, let the user know how dangerous the problem is, and list possible fixes to that code. While currently running on a Command Line Interface I will be working on a Graphical User Interface after I finish the vehicle search feature.

The source java file requires an external third party library Jsoup, however, a runnable jar file is included with the required library JAR files included.

The easiest way to use this program is to download the runnable jar file, otherwise you can download the jar file with the external libraries include and run from that directory. The raw source code is listed under source.

Current Version is 1.0.0

Release notes:
1.0.0 - Initial release. Search up a valid OBDII code and it will verify the format, return the code, code title, description, severity (if included on information page), symptoms, and causes of the code.

Link to Jsoup Library: https://jsoup.org/
