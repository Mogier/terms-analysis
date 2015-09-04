package treegenerator.model;

import treegenerator.model.OnlineConcept;
import edu.smu.tspell.wordnet.NounSynset;

public class WordNetConcept extends OnlineConcept {
	protected NounSynset synset;
	
	public WordNetConcept(NounSynset wordnetDescription, int idConcept, Integer base) {
		super("Wordnet:"+wordnetDescription.getWordForms()[0].toLowerCase(),TypeTerm.Wordnet, idConcept, base, wordnetDescription.getWordForms()[0]);
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
