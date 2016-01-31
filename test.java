import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class Word implements Comparable<Word> {
	public String word;
	Word(String s) {
		word = s;
	}

	@Override
	public int compareTo(Word another) {
		if (this.word.compareTo(another.word) < 0) {
		    return -1;
		} else {
		    return 1;
		}
	}

	@Override
	public boolean equals(Object wc) {
		if (wc instanceof Word){
            Word ptr = (Word) wc;
            return word.equals(ptr.word);
        }
        return false;
	}
}

class test {
	public static void main(String[] args) {
		String abc = "a b c d e";
		String[] split = abc.split(" ");
		List<String> list = Arrays.asList(split);
		List<Word> words = new ArrayList<>();
		for (String s : list) {
			Word w = new Word(s);
			words.add(w);
		}

		for (Word w : words) {
			System.out.println(w.word);
		}

		String temp = "a";
		Word temp2 = new Word(temp);

		if (words.contains(temp2)) {
			System.out.println("\n\nYup");
		} else {
			System.out.println("\n\nNope");
		}

		// if (words.indexOf(temp2) == -1) {
		// 	System.out.println("\n\nNope");
		// } else {
		// 	System.out.println("\n\nYup");
		// }

		if (words.get(0).equals(temp2)) {
			System.out.println("\n\nYup Yup");
		} else {
			System.out.println("\n\nNope Nope");
		}
	}
}