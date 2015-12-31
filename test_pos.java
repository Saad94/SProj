
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

class test_pos {
  private test_pos() {}

  public static void main(String[] args) throws Exception {
    String fileName = "textfiles\\tinyfile.txt";
    String modelFile = "models\\english-left3words-distsim.tagger";

    // Open the file.
    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
    
    // Open an output stream.
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"));

    // The main class for users to run, train, and test the part of speech tagger.
    // http://www-nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/tagger/maxent/MaxentTagger.html
    MaxentTagger tagger = new MaxentTagger(modelFile);

    // Produces a list of sentences from the document.
    // http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/DocumentPreprocessor.html
    DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
    
    // A fast, rule-based tokenizer implementation, which produces Penn Treebank style tokenization of English text.
    // http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
    TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "untokenizable=noneKeep");
    documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);

    // Go through each sentence in the document.
    for (List<HasWord> sentence : documentPreprocessor) {
      
      // Tag each sentence, producing a list of tagged words.
      List<TaggedWord> tSentence = tagger.tagSentence(sentence);

      // Print the sentence
      pw.println(Sentence.listToString(tSentence, false));

      // Go through each tagged word in the sentence.
      for (TaggedWord tWord : tSentence) {
        
        // Check if the tagged word is a verb of any kind (there are 6 kinds of verbs; HOW_TO_RUN.txt).
        if (tWord.tag().startsWith("VB")) {
          pw.println("\tTAG: " + tWord.tag() + "\tWORD: " + tWord.word());
        }
      }

      pw.println("\n");
    }

    pw.close();
  }
}
