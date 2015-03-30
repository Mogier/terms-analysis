# TreeGenerator / terms-analysis
This program create a semantic forest based on the words on input and edit a .gexf file. Words are singularized before treatment.

##Configuration
In order to run this program you'll need to :
- Download a Wordnet dictionary. Current version can be found [here](https://wordnet.princeton.edu/wordnet/download/current-version/), i've worked with the 3.0 version.  Please do not DL the *DATABASE-ONLY* file.
- Make sure to have an Internet access, else you won't be able to reach DBpedia endpoints.
- Create the directory for generated files.
- Open the project in Eclipse.

## Launch arguments
Please call the main method that way :
- arg[0] : *TEXT_REQUEST* 
- arg[1] : *SEPARATOR*
- arg[2] : *PATH_TO_INI_FILE*
- arg[3] : *GEXF_EXPORT_DIRECTORY*
- arg[4] : *File name* (no .gext needed)

Example : "animal theme,differential focus,no person,outdoor,shell,antennae,edible snail," "," "config.ini" "generatedFiles/" "000"