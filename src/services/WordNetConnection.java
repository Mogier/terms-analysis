package services;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetConnection {
	
	public Synset[] getNounSynsets(String text) {
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] returnsynsets = database.getSynsets(text, SynsetType.NOUN);

		return returnsynsets;
	}
	
	public void printHyponyms(String text){
		NounSynset nounSynset; 
		NounSynset nounSynset2; 
		NounSynset[] hyponyms; 

		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets(text, SynsetType.NOUN); 
		for (int i = 0; i < synsets.length; i++) { 
		    nounSynset = (NounSynset)(synsets[i]); 
		    hyponyms = nounSynset.getHyponyms(); 
		    System.err.println(nounSynset.getWordForms()[0] + 
		            ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms"); 
		    for (int j = 0; j<hyponyms.length;j++) {
		    	nounSynset2 = (NounSynset) hyponyms[j];
		    	System.out.println(nounSynset2.getWordForms()[0]);
		    }
		}
	}

}
