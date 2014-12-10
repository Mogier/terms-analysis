package services;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetConnection {
	
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
	
	public void printHypernyms(String text){
		NounSynset nounSynset; 
		NounSynset nounSynset2; 
		NounSynset[] hyper; 

		WordNetDatabase db = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = db.getSynsets(text, SynsetType.NOUN); 
		for (int i = 0; i < synsets.length; i++) { 
		    nounSynset = (NounSynset)(synsets[i]); 
		    hyper = nounSynset.getHypernyms(); 
		    System.err.println(nounSynset.getWordForms()[0] + 
		            ": " + nounSynset.getDefinition() + ") has " + hyper.length + " hypernyms"); 
		    for (int j = 0; j<hyper.length;j++) {
		    	nounSynset2 = (NounSynset) hyper[j];
		    	System.out.println(nounSynset2.getWordForms()[0]);
		    }
		}
	}

}
