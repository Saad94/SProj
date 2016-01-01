
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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
  // static List<String>   phrases_causal            = Arrays.asList("because", "for this reason", "for that reason", "consequently", "as a consequence of", "as a result of");
  // static List<String>   phrases_non_causal        = Arrays.asList("but", "in short", "in other words", "whereas", "on the other hand", "nevertheless", "nonetheless", "in spite of", "in contrast", "however", "even", "though", "despite the fact", "conversely", "although"); 
  // static List<Integer>  count_phrases_causal      = Arrays.asList(0, 0, 0, 0, 0, 0);
  // static List<Integer>  count_phrases_non_causal  = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  static List<File>     all_files         = new ArrayList<>();
  static List<String>   phrases_all       = Arrays.asList("because", "for this reason", "for that reason", "consequently", "as a consequence of", "as a result of", "but", "in short", "in other words", "whereas", "on the other hand", "nevertheless", "nonetheless", "in spite of", "in contrast", "however", "even", "though", "despite the fact", "conversely", "although");
  static List<Integer>  count_phrases_all = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  static String         dirName           = System.getProperty("user.dir") + "\\textfiles\\test";
  static String         modelFile         = "models\\english-left3words-distsim.tagger";
  static PrintWriter    pw;


  //=================================================================================
  //=================================================================================

  public static void IDF_Counter(int id) throws Exception {
    String content = new Scanner(all_files.get(id)).useDelimiter("\\Z").next().toLowerCase();
    for (int i = 0; i < phrases_all.size(); i++) {
      if (content.contains(phrases_all.get(i))) {
        count_phrases_all.set(i, count_phrases_all.get(i)+1);
      }
    }
  }

  public static void IDF() {

  }

  //=================================================================================
  //=================================================================================

  static void iterateFiles(File[] files) {
    for (File file : files) {
      if (file.isDirectory()) {
        iterateFiles(file.listFiles());
      } else if (file.isFile()) {
        if (file.getPath().endsWith(".txt")) {
          all_files.add(file);
        }
      }
    }
  }

  //=================================================================================
  //=================================================================================

  public static void main(String[] args) throws Exception {
    pw = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"));
    
    // Get list of all files which have to be parsed in order to construct the (non-)Causal verb-pairs.
    File[] files = new File(dirName).listFiles();
    iterateFiles(files);

    // The main class for users to run, train, and test the part of speech tagger.
    // http://www-nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/tagger/maxent/MaxentTagger.html
    MaxentTagger tagger = new MaxentTagger(modelFile);

    // A fast, rule-based tokenizer implementation, which produces Penn Treebank style tokenization of English text.
    // http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
    TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "untokenizable=noneKeep");

    // Go through each file in the list.
    for (int id = 0; id < all_files.size(); id++) {

      // Print each file's name.
      String fileName = all_files.get(id).getPath();
      pw.print("\n***\n" + fileName + "\n***\n");

      // Check for occurrences for the (non-)causal strings in the current document, incremement the occurrence counter for use in IDF function.
      IDF_Counter(id);

      // Open the file.
      BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
      
      // Produces a list of sentences from the document.
      // http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/DocumentPreprocessor.html
      DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
      documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);

      // Go through each sentence in the document.
      for (List<HasWord> sentence : documentPreprocessor) {
        
        // Print the sentence
        String sentenceString = Sentence.listToString(sentence, false).toLowerCase();
        pw.println(sentenceString);

        // Tag each sentence, producing a list of tagged words.
        List<TaggedWord> tSentence = tagger.tagSentence(sentence);

        // Print the tagged sentence
        pw.println(Sentence.listToString(tSentence, false));

        // Go through each tagged word in the sentence.
        for (TaggedWord tWord : tSentence) {
          
          // Check if the tagged word is a verb of any kind (there are 6 kinds of verbs; HOW_TO_RUN.txt).
          if (tWord.tag().startsWith("VB")) {
            if (tWord.tag().equals("VB")) {
              pw.println("\tTAG: " + tWord.tag() + " \tWORD: " + tWord.word());  
            } else {
              pw.println("\tTAG: " + tWord.tag() + "\tWORD: " + tWord.word());  
            }
          }
        }

        pw.println("\n");
      }
    }

    pw.print("Inverse Document Frequency Count\n\tCausal:     ");
    for (int i = 0; i < count_phrases_all.size(); i++) {
      if (i == 6) {pw.print("\n\tNon-Causal: ");}
      pw.print(count_phrases_all.get(i) + ", ");
    }
    pw.print("\n\n");

    pw.close();
  }
}
