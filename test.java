import java.util.List;
import java.util.Arrays;

class test {
	public static void main(String[] args) {
		String abc = "however";
		String[] split = abc.split(" ");
		List<String> list = Arrays.asList(split);
		for (String s : list) {
			System.out.println(s);
		}
	}
}