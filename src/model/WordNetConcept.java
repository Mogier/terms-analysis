package model;

import edu.smu.tspell.wordnet.NounSynset;
import model.OnlineConcept;

public class WordNetConcept extends OnlineConcept {
	protected NounSynset synset;
	
	public WordNetConcept(NounSynset wordnetDescription, int idConcept, boolean base) {
		super("Wordnet:"+wordnetDescription.getWordForms()[0],TypeTerm.Wordnet, idConcept, base, wordnetDescription.getWordForms()[0]);
		synset = wordnetDescription;
		System.out.println("Creating WordNet Concept "+id+" " + label + " : " + uri);
	}	
	
	public NounSynset getSynset() {
		return synset;
	}

	public void setSynset(NounSynset synset) {
		this.synset = synset;
	}
	
	
}
