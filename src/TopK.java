//Konstantinos Andreou 4316
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class TopK {
	private int k=5;
	
	public int inputK() {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter the k:");
		
		k = input.nextInt();
		if (k <= 0) {
			System.out.println("Exiting,k must be a positive integer bigger than 0");
			System.exit(-1);
		}
		input.close();
		return k;
	}	
	
	public float[] readRndFile() {
		BufferedReader br;
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		try {
			br = new BufferedReader(new FileReader("rnd.txt"));	
			while ((line = br.readLine()) != null) {
			    lines.add(line);		
			}
		} catch (IOException e) {}		
		float[] R = new float[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			String[] lineValues = lines.get(i).split(" ");
			R[Integer.parseInt(lineValues[0])] = Float.parseFloat(lineValues[1]);
		}
		return R;
	}
	
	public PriorityQueue<Float> readSeqFiles() {
		float[] R = readRndFile();
		HashMap<Integer, Float> lowerScores = new HashMap<Integer, Float>();	
		HashMap<Integer, Float> totalScores = new HashMap<Integer, Float>();	
		PriorityQueue<Float> minHeap = null;
		float threshold = 0;
		int accesses = 0;
		BufferedReader seq1;
		BufferedReader seq2;
		String line1, line2 = "";
		try {
			seq1 = new BufferedReader(new FileReader("seq1.txt"));	
			seq2 = new BufferedReader(new FileReader("seq2.txt"));
			while (((line1 = seq1.readLine()) != null && (line2 = seq2.readLine()) != null) || minHeap.peek() < threshold) {
				float value1, value2 = 0;
				String[] lineValues = line1.split(" ");
				int id = Integer.parseInt(lineValues[0]);
				value1 = Float.parseFloat(lineValues[1]);
				if (!lowerScores.containsKey(id)) {
					lowerScores.put(id, R[id]+value1); 
				} else {
					totalScores.put(id, lowerScores.get(id)+value1);				
				}
				threshold = value1 + value2 + 5f;
				if (minHeap != null) {
					if (minHeap.peek() <= value1) {
						minHeap.poll();
						minHeap.add(e)
						return minHeap;
					}
				}
				accesses++;
				if (minHeap == null) {
					if (totalScores.size() + lowerScores.size() == k) {
						ArrayList<Float> concatenatedList = new ArrayList<Float>();
						concatenatedList.addAll(lowerScores.values());
						concatenatedList.addAll(totalScores.values());
						minHeap = new PriorityQueue<Float>(concatenatedList);
					}
				}
				lineValues = line2.split(" ");
				id = Integer.parseInt(lineValues[0]);
				value2 = Float.parseFloat(lineValues[1]);
				if (!lowerScores.containsKey(id)) {
					lowerScores.put(id, R[id]+value2); 
				} else {
					totalScores.replace(id, lowerScores.get(id)+value2);
				}
				threshold = value1 + value2 + 5f;
				if (minHeap != null) {
					if (minHeap.peek() >= threshold) {
						return minHeap;
					}
				}
				accesses++;	
				if (minHeap == null) {
					if (totalScores.size() + lowerScores.size() == k) {
						ArrayList<Float> concatenatedList = new ArrayList<Float>();
						concatenatedList.addAll(lowerScores.values());
						concatenatedList.addAll(totalScores.values());
						minHeap = new PriorityQueue<Float>(concatenatedList);
					}
				}
			}
		} catch (IOException e) {}	
		//System.out.println(accesses);
		return minHeap;
	}
	
	public void checkBruteForce(){ //round error einai ok na uparxei, giati tha uparxei kai stous duo pinakes
		float[] R = readRndFile();
		BufferedReader seq1;
		BufferedReader seq2;
		String line1, line2;
		try {
			seq1 = new BufferedReader(new FileReader("seq1.txt"));
			seq2 = new BufferedReader(new FileReader("seq2.txt"));	
			while ((line1 = seq1.readLine()) != null && (line2 = seq2.readLine()) != null) {
				String[] lineValues = line1.split(" ");
				sum(R, lineValues); 	
				lineValues = line2.split(" ");
				sum(R, lineValues);
			}
		} catch (IOException e) {}
		FloatWithIndex[] arrWithIndex = new FloatWithIndex[R.length];
        for (int i = 0; i < R.length; i++) {
            arrWithIndex[i] = new FloatWithIndex(R[i], i);
        }
        Arrays.sort(arrWithIndex, new Comparator<FloatWithIndex>() {
            @Override
            public int compare(FloatWithIndex f1, FloatWithIndex f2) {
                return Float.compare(f2.value, f1.value);
            }
        });
        System.out.println("Brute force all entries sorted");
        for (int i = 0; i < R.length; i++) {
            System.out.println(arrWithIndex[i].index + ": " + String.format("%.2f", arrWithIndex[i].value));
        }
	}

	private void sum(float[] R, String[] lineValues) { 
		int index = Integer.parseInt(lineValues[0]);
		float value = Float.parseFloat(lineValues[1]);
		R[index] += value;
	}
	
	private class FloatWithIndex {
	    float value;
	    int index;

	    public FloatWithIndex(float value, int index) {
	        this.value = value;
	        this.index = index;
	    }
	}
	
	public static void main(String[] args) {
		TopK topK = new TopK();
		//topK.inputK();
		/*
		float[] R = topK.readRndFile();
		for (Float i : R) {
			System.out.println(i);
		}
		*/
		
		//topK.checkBruteForce();
		PriorityQueue<Float> minHeap = topK.readSeqFiles();
		System.out.println(minHeap);
	}
}
