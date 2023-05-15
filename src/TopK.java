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

public class TopK {
	int k;
	
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
		ArrayList<Record> records = new ArrayList<Record>(); //used for faster iteration in checkTermination method
		PriorityQueue<Record> minHeap = new PriorityQueue<Record>();
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
					records.add(record);
				} else {
					recordsFound.get(id).setTotalScore(value1);		
				}
				accesses++;
				threshold = value1 + value2 + 5f;
				
				if (minHeap.isEmpty()) {
					minHeap = initialiseMinHeap(recordsFound);	
				} else {
					checkPush(recordsFound, minHeap, id);	
					if (minHeap.peek().getCurrentScore() >= threshold) { //checking for termination
						if (checkTermination(records, minHeap, value1, value2) == true) {
							System.out.println("Number of sequential accesses= " + accesses);
							return minHeap;
						}
					}
				}
				
				//seq2
				lineValues = line2.split(" ");
				id = Integer.parseInt(lineValues[0]);
				value2 = Float.parseFloat(lineValues[1]);
				
				if (!recordsFound.containsKey(id)) {
					Record record = new Record(id, R[id]+value2, 2);
					recordsFound.put(id, record);
					records.add(record);
				} else {
					recordsFound.get(id).setTotalScore(value2);
				}
				accesses++;
				threshold = value1 + value2 + 5f;	
				
				if (minHeap.isEmpty()) {
					minHeap = initialiseMinHeap(recordsFound);	
				} else {
					checkPush(recordsFound, minHeap, id);	
					if (minHeap.peek().getCurrentScore() >= threshold) { //checking for termination
						if (checkTermination(records, minHeap, value1, value2)== true) {
							System.out.println("Number of sequential accesses= " + accesses);
							return minHeap;
						}
					}
				}
			}			
		} catch (IOException e) {}		
		System.out.println("Number of sequential accesses= " + accesses);		
		return minHeap;
	}
	
	private boolean checkTermination(ArrayList<Record> records, PriorityQueue<Record> minHeap,
			float value1, float value2) {
		float upperBound = 0;
		for (Record record : records) {
			if (!minHeap.contains(record)) {
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
		if (minHeap.contains(recordsFound.get(id))) {
			minHeap.remove(recordsFound.get(id));
			minHeap.add(recordsFound.get(id));
		} else if (minHeap.peek().getCurrentScore() < recordsFound.get(id).getCurrentScore()) {
			minHeap.poll();
			minHeap.add(recordsFound.get(id));
		}
	}

	private PriorityQueue<Record> initialiseMinHeap(HashMap<Integer, Record> recordsFound) { 
		if (recordsFound.size() == k) {
			return new PriorityQueue<Record>(recordsFound.values());
		}
		return new PriorityQueue<>();
	}
	
	public void printTopKs(PriorityQueue<Record> minHeap) {
		//FloatWithIndex[] bruteForce = checkBruteForce();
		ArrayList<Record> tops = new ArrayList<Record>();
		for (int i = 0; i < k; i++) {
			Record record = minHeap.poll();
			tops.add(record);
		}
		Collections.reverse(tops);
		System.out.println("Top k objects:");	
		for (int i = 0; i < k; i++) {
			System.out.println(tops.get(i).getId() + ": " + String.format("%.2f", tops.get(i).getTotalScore()));
			//print topK next to brute force
			//System.out.println(counter + " " + tops.get(i).getId() + ": " + String.format("%.2f", tops.get(i).getTotalScore()) + "	" + bruteForce[i].index + ": " + String.format("%.2f", bruteForce[i].value));
		}
	}
	
	public FloatWithIndex[] checkBruteForce(){ 
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
        return arrWithIndex;
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
		topK.k = Integer.parseInt(args[0]);
		//topK.checkBruteForce();
		PriorityQueue<Record> minHeap = topK.readSeqFiles();
		topK.printTopKs(minHeap);
	}	
}