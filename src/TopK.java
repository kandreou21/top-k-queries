//Konstantinos Andreou 4316
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

public class TopK {
	private int k;
	
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

	public PriorityQueue<Record> readSeqFiles() {
		float[] R = readRndFile();
		HashMap<Integer, Record> recordsFound = new HashMap<Integer, Record>();
		PriorityQueue<Record> minHeap = null;
		float threshold = 0;
		int accesses = 0;
		BufferedReader seq1, seq2;
		String line1, line2 = "";
		try {
			seq1 = new BufferedReader(new FileReader("seq1.txt"));	
			seq2 = new BufferedReader(new FileReader("seq2.txt"));
			float value1, value2 = 0;
			while ((line1 = seq1.readLine()) != null && (line2 = seq2.readLine()) != null) {
				String[] lineValues = line1.split(" ");
				int id = Integer.parseInt(lineValues[0]);
				value1 = Float.parseFloat(lineValues[1]);
				
				if (!recordsFound.containsKey(id)) {
					Record record = new Record(id, R[id]+value1, 1);
					recordsFound.put(id, record);	
				} else {
					recordsFound.get(id).setTotalScore(value1);		
				}
				if (minHeap == null) {
					minHeap = initialiseMinHeap(recordsFound, minHeap);	
				} else {
					checkPush(recordsFound, minHeap, id);	
					if (minHeap.peek().getCurrentScore() >= threshold) { //checking for termination
						boolean stopFlag = checkTermination(recordsFound, minHeap, value1, value2);
						if (stopFlag == true) {
							System.out.println("Number of sequential accesses= " + accesses);
							return minHeap;
						}
					}
				}
				accesses++;
				threshold = value1 + value2 + 5f;

				//seq2
				lineValues = line2.split(" ");
				id = Integer.parseInt(lineValues[0]);
				value2 = Float.parseFloat(lineValues[1]);
				
				if (!recordsFound.containsKey(id)) {
					Record record = new Record(id, R[id]+value2, 2);
					recordsFound.put(id, record);
				} else {
					recordsFound.get(id).setTotalScore(value2);
				}
				if (minHeap == null) {
					minHeap = initialiseMinHeap(recordsFound, minHeap);	
				} else {
					checkPush(recordsFound, minHeap, id);	
					if (minHeap.peek().getCurrentScore() >= threshold) { //checking for termination
						boolean stopFlag = checkTermination(recordsFound, minHeap, value1, value2);
						if (stopFlag == true) {
							System.out.println("Number of sequential accesses= " + accesses);
							return minHeap;
						}
					}
				}
				accesses++;
				threshold = value1 + value2 + 5f;	
			}			
		} catch (IOException e) {}		
		System.out.println("Number of sequential accesses= " + accesses);		
		return minHeap;
	}

	private boolean checkTermination(HashMap<Integer, Record> recordsFound, PriorityQueue<Record> minHeap,
			float value1, float value2) {
		for (Record record : recordsFound.values()) {
			if (!minHeap.contains(record)) {
				float upperBound;
				if (record.fileShown == 1) {
					upperBound = record.getLowerBound() + value2;  
				} else {
					upperBound = record.getLowerBound() + value1;  
				}
				if (upperBound > minHeap.peek().getCurrentScore()) {
					return false;
				}	
			} 
		}
		return true;
	}

	private void checkPush(HashMap<Integer, Record> recordsFound, PriorityQueue<Record> minHeap, int id) {
		if (!minHeap.contains(recordsFound.get(id))) {
			if (minHeap.peek().getCurrentScore() <= recordsFound.get(id).getCurrentScore()) {
				minHeap.poll();
				minHeap.add(recordsFound.get(id));
			}
		}
	}

	private PriorityQueue<Record> initialiseMinHeap(HashMap<Integer, Record> recordsFound, PriorityQueue<Record> minHeap) {
		if (recordsFound.size() == k) {
			minHeap = new PriorityQueue<Record>(recordsFound.values());
		}
		return minHeap;
	}
	
	public void checkBruteForce(){ 
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
            System.out.println(i + " " +arrWithIndex[i].index + ": " + String.format("%.2f", arrWithIndex[i].value));
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
	
	public void printTopKs(PriorityQueue<Record> minHeap) {
		ArrayList<Record> tops = new ArrayList<Record>();
		int counter = 0;
		for (int i = 0; i < k; i++) {
			Record record = minHeap.poll();
			tops.add(record);
		}
		Collections.reverse(tops);
		System.out.println("Top k objects:");	
		for (Record record : tops) {
			System.out.println(counter + " " + record.getId() + ": " + String.format("%.2f", record.getCurrentScore()));
			counter++;
		}
	}
	
	public static void main(String[] args) {
		TopK topK = new TopK();
		topK.inputK();
		//topK.checkBruteForce();
		PriorityQueue<Record> minHeap = topK.readSeqFiles();
		topK.printTopKs(minHeap);
	}	
}