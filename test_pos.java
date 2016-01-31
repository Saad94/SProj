
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Set;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//=================================================================================
//=================================================================================

class Word_Count implements Comparable<Word_Count> {
  public int    actualCount;
  public int    documentCount;
  public String word;

  public Word_Count(String w1) {
    word          = w1.toLowerCase();
    actualCount   = 1;
    documentCount = 1;
  }

  public String print() {
    return Integer.toString(documentCount) + "            " + Integer.toString(actualCount) + "          " + word;
  }

  public boolean equals(Word_Count wc) {
    return word.equals(wc.word);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Word_Count) {
      Word_Count wc = (Word_Count) o;
      return word.equals(wc.word);
    }
    return false;
  }

  public void documentIncrement() {
    documentCount++;
  }

  public void documentIncrement(int inc) {
    documentCount += inc;
  }

  public void actualIncrement() {
    actualCount++;
  }

  public void actualIncrement(int inc) {
    actualCount += inc;
  }

  @Override
  public int compareTo(Word_Count another) {
    if (this.word.compareTo(another.word) < 0) {
        return -1;
    } else {
        return 1;
    }
  }
}


class Word_Pair implements Comparable<Word_Pair> {
  public int    actualCount;
  public int    documentCount;
  public String word_one;
  public String word_two;

  public Word_Pair(String w1, String w2) {
    w1 = w1.toLowerCase();
    w2 = w2.toLowerCase();
    if (w1.compareTo(w2) < 0) {
      word_one = w1;
      word_two = w2;
    } else {
      word_one = w2;
      word_two = w1;
    }

    actualCount = 1;
    documentCount = 1;
  }

  public String toString() {
    return word_one + " - " + word_two;
  }

  public String print() {
    return Integer.toString(documentCount) + "\t" + Integer.toString(actualCount) + "\t" + toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Word_Pair) {
      Word_Pair wp = (Word_Pair) o;
      return word_one.equals(wp.word_one) && word_two.equals(wp.word_two);
    }
    return false;
  }

  public boolean equals(Word_Pair wp) {
    return word_one.equals(wp.word_one) && word_two.equals(wp.word_two);
  }

  public void documentIncrement() {
    documentCount++;
  }

  public void documentIncrement(int inc) {
    documentCount += inc;
  }

  public void actualIncrement() {
    actualCount++;
  }

  public void actualIncrement(int inc) {
    actualCount += inc;
  }

  @Override
  public int compareTo(Word_Pair another) {
    if (this.toString().compareTo(another.toString()) < 0) {
        return -1;
    } else {
        return 1;
    }
  }
}

//=================================================================================
//=================================================================================

public class test_pos {
  static List<File>       all_files         = new ArrayList<>();
  static List<Word_Pair>  all_verb_pairs    = new ArrayList<>();
  static List<Word_Count> doc_count_words   = new ArrayList<>();
  static List<String>     phrases_all       = Arrays.asList("because", "for this reason", "for that reason", "consequently", "as a consequence of", "as a result of", "but", "in short", "in other words", "whereas", "on the other hand", "nevertheless", "nonetheless", "in spite of", "in contrast", "however", "even", "though", "despite the fact", "conversely", "although");
  static List<Integer>    length_phrases_all= Arrays.asList(1, 3, 3, 1, 4, 4, 1, 2, 3, 1, 4, 1, 1, 3, 2, 1, 1, 1, 3, 1, 1);
  static String           dirName           = System.getProperty("user.dir") + "\\textfiles\\test";
  static String           modelFile         = "models\\english-left3words-distsim.tagger";
  static PrintWriter      pw;
  static int              totalNumWords;

  //=================================================================================
  //=================================================================================

  public static Word_Count find_WC(String word) {
    for (Word_Count wc : doc_count_words) {
      if (wc.word.equals(word)) {
        return wc;
      }
    }
    return null;
  }

  //=================================================================================
  //=================================================================================

  public static List<Integer> docWordCounter(int id) throws Exception {
    List<Integer> returnValue = new ArrayList<>();
    String content = new Scanner(all_files.get(id)).useDelimiter("\\Z").next().toLowerCase();
    for (int i = 0; i < phrases_all.size(); i++) {
      if (content.contains(phrases_all.get(i))) {
        returnValue.add(i);
      }
    }

    content = content.replace(".", "");
    content = content.replace(",", "");

    // Increment document counter for words. Add new words to doc_count_words.
    Set<String> set = new HashSet<String>(Arrays.asList(content.split(" ")));

    for (String s : set) {
      Word_Count temp = new Word_Count(s);
      int index = doc_count_words.indexOf(temp);
      if (index != -1) {
        doc_count_words.get(index).documentIncrement();
      } else {
        doc_count_words.add(temp);
        temp.actualIncrement(-1);
      }
    }

    // Increment actual counter for words.
    for (Word_Count wc : doc_count_words) {
      int i = 0;
      Pattern p = Pattern.compile(wc.word);
      Matcher m = p.matcher(content);
      while (m.find()) {
          i++;
      }
      wc.actualIncrement(i);
    }
    
    return returnValue;
  }

  //=================================================================================
  //=================================================================================

  public static double IDF(Word_Pair wp) {
    double one = idf(wp.word_one);
    double two = idf(wp.word_two);
    double three = idf(wp);

    pw.print(wp.print() + "\n\t");
    pw.println(Double.toString(one) + " * " + Double.toString(two) + " * " + Double.toString(three));
    return one * two * three;
  }

  public static double idf(String word) {
    double ans = 1.0 + find_WC(word).documentCount;
    return all_files.size() / ans;
  }

  public static double idf(Word_Pair wp) {
    double ans = 1.0 + wp.documentCount;
    return all_files.size() / ans;
  }

  //=================================================================================
  //=================================================================================

  public static double P(String word) {
    return 0.0;
  }

  public static double P(Word_Pair wp) {
    return 0.0;
  }

  public static double PMI(Word_Pair wp) {
    return Math.log(P(wp) / (P(wp.word_one) * P(wp.word_two)));
  }

  //=================================================================================
  //=================================================================================

  public static double CD(Word_Pair wp) {
    return PMI(wp) * max(wp) * IDF(wp);
  }

  //=================================================================================
  //=================================================================================

  public static double max(Word_Pair wp) {
    return 0.0;
  }

  //=================================================================================
  //=================================================================================

  public static double PS_I(Word_Pair wp) {
    return 0.0;
  }

  //=================================================================================
  //=================================================================================

  public static Word_Pair f_I() {
    return null;
  }

  //=================================================================================
  //=================================================================================

  public static void findVerbPairs(List<Integer> toCheck, List<TaggedWord> tSentence) {
    boolean printer = true;

    // Go through each (non-)causal phrase to be checked in the current sentence.
    for (Integer id : toCheck) {
      List<String> phrase = Arrays.asList(phrases_all.get(id).split(" "));

      // Go through each tagged word in the sentence.
      for (int i = 0; i < tSentence.size(); i++) {

        // Find the position of the (non-)causal phrase.
        boolean found = false;
        if (tSentence.get(i).word().toLowerCase().equals(phrase.get(0))) {
          found = true;
          if (phrase.size() > 1) {
            for (int j = 1; j < phrase.size(); j++) {
              if (tSentence.size() <= i+j || !tSentence.get(i+j).word().toLowerCase().equals(phrase.get(j))) {
                found = false;
                break;
              }
            }
          }

          if (found) {
            List<String> verbsBefore = new ArrayList<>();
            List<String> verbsAfter  = new ArrayList<>();

            // Check for verbs occurring before the unambiguous discourse marker.
            for (int j = 0; j < i; j++) {
              if (tSentence.get(j).tag().startsWith("VB")) {
                verbsBefore.add(tSentence.get(j).word());
              }
            }

            // Check for verbs occurring after the unambiguous discourse marker.
            for (int j = i+length_phrases_all.get(id); j < tSentence.size(); j++) {
              if (tSentence.get(j).tag().startsWith("VB")) {
                verbsAfter.add(tSentence.get(j).word());
              }
            }

            // pw.println("BEFORE");
            // for (Word_Pair wp : all_verb_pairs) {
            //   pw.println(wp.print());
            // }
            // pw.println("\n\n");

            // If verbs exist both before and after the discourse marker, form all possible pairs of them.
            for (String s1 : verbsBefore) {
              for (String s2 : verbsAfter) {

                // if the verbs are different
                if (!s1.toLowerCase().equals(s2.toLowerCase())) {
                  all_verb_pairs.add(new Word_Pair(s1, s2));
                }
              }
            }

            // pw.println("AFTER");
            // for (Word_Pair wp : all_verb_pairs) {
            //   pw.println(wp.print());
            // }
            // pw.println("\n\n");

            // Removing duplicate Verb Pairs and incrementing their count.
            Collections.sort(all_verb_pairs);
            int size = all_verb_pairs.size() - 1;
            for (int j = 0; j < size; j++) {
              if (all_verb_pairs.get(j).equals(all_verb_pairs.get(j+1))) {
                all_verb_pairs.get(j).actualIncrement();
                all_verb_pairs.remove(j+1);
                j--;
                size--;
              }
            }
          }
        }

        // Check if the tagged word is a verb of any kind (there are 6 kinds of verbs; HOW_TO_RUN.txt).
        if (printer && tSentence.get(i).tag().startsWith("VB")) {
          if (tSentence.get(i).tag().equals("VB")) {
            pw.println("\tTAG: " + tSentence.get(i).tag() + " \tWORD: " + tSentence.get(i).word());  
          } else {
            pw.println("\tTAG: " + tSentence.get(i).tag() + "\tWORD: " + tSentence.get(i).word());  
          }
        }
      }
      printer = false;
    }
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

      // Check for occurrences for the (non-)causal strings in the current document, increment the occurrence counter for use in IDF function.
      // Find out which (non-)causal string to check for in the current document
      List<Integer> toCheck = docWordCounter(id);

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

        // Print the tagged sentence.
        pw.println(Sentence.listToString(tSentence, false));

        // Make a backup of all_verb_pairs so we can check for differences and increment document count.
        List<Word_Pair> oldList = new ArrayList<>();
        List<Integer> oldActualCounts = new ArrayList<>();
        for (Word_Pair wp : all_verb_pairs) {
          oldList.add(wp);
          int i = wp.actualCount;
          oldActualCounts.add(i);
        }

        // Find pairs of verbs before and after the unambiguous discourse markers.
        findVerbPairs(toCheck, tSentence);

        // Incrementing the document count for the pairs.
        for (int i = 0; i < oldList.size(); i++) {
          if (oldList.get(i).actualCount != oldActualCounts.get(i)) {
            oldList.get(i).documentIncrement();
          }
        }

        pw.println("\n");
      }
    }

    // Total number of words.
    totalNumWords = 0;
    ////////////////////////////////////////////////////////////////////

    // Printing the Inverse Document Frequency Count
    pw.print("DOCUMENT     ACTUAL     WORD\n");
    Collections.sort(doc_count_words);
    for (int i = 0; i < doc_count_words.size(); i++) {
      pw.println(doc_count_words.get(i).print());
    }
    pw.print("\n\n");

    // Printing the Verb Pairs.
    pw.print("Verb Pairs\n");
    for (Word_Pair wp : all_verb_pairs) {
      pw.println("\t" + wp.print());
    }

    // Printing a sample IDF.
    pw.print("\nIDF\n\t");
    IDF(all_verb_pairs.get(14));

    pw.close();
  }
}
