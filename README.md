# TreeGenerator / terms-analysis
This program create a semantic forest based on the words on input. It can also create a .gexf file which represent the forest. Words are lemmatized by the *Inflector* Java class before treatment.

## Context
This project was used for my master thesis, see [this repository](https://github.com/Mogier/master-thesis) for the complete thesis report.

## Configuration
In order to run this program you'll need to :
- Download a Wordnet dictionary. Current version can be found [here](https://wordnet.princeton.edu/wordnet/download/current-version/), I've worked with the 3.0 version.  Please do not DL the *DATABASE-ONLY* file.
- Make sure to have an Internet access, else you won't be able to reach DBpedia endpoints.
- Create the directory for generated files.
- Open the project in Eclipse.
- Edit the *config.ini* file with the followings :
	- *wordnetAbsolutePath* : Absolute path to the *dict* subdirectory of your WordNet dataset
	- *rdf* : rdf PREFIX for SPARQL queries
	- *rdfs* : rdfs PREFIX for SPARQL queries

## Launch arguments
Please call the main method that way :
- arg[0] : *TEXT_REQUEST* 
- arg[1] : *SEPARATOR*
- arg[2] : *PATH_TO_INI_FILE*
- arg[3] : *GEXF_EXPORT_DIRECTORY*
- arg[4] : *File name* (no .gext needed)

Example : "animal theme,differential focus,no person,outdoor,shell,antennae,edible snail," "," "config.ini" "generatedFiles/" "000"