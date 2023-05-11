public class MinHeap {
	private int[] Heap;
	private int size;
	private int maxsize;
	private static final int FRONT=1;
	
	public MinHeap(int maxsize){
		this.size = 0;
		this.maxsize = maxsize;
		Heap = new int[this.maxsize+1];
		Heap[0] = Integer.MIN_VALUE;
	}
	
	private int parent(int i){
		return i/2;
	}
	
	private int leftChild(int i){
		return i*2;
	}
	
	private int rightChild(int i){
		return i*2+1;
	}
	
	private boolean isLeaf(int i){
		if(i>=size/2 && i<=size)
			return true;
		else
			return false;
	}
	
	private void swap(int i,int j){
		int tmp = Heap[i];
		Heap[i] = Heap[j];
		Heap[j] = tmp;
	}
	
	private void minHeapify(int root){
		if(!isLeaf(root)){
			if(Heap[root]>Heap[leftChild(root)] || Heap[root]>Heap[rightChild(root)]){
				if(Heap[leftChild(root)] < Heap[rightChild(root)]){
					swap(root,leftChild(root));
					minHeapify(leftChild(root));
				}else{
					swap(root,rightChild(root));
					minHeapify(rightChild(root));
				}
			}
		}
	}
	
	public void insert(int element){
		Heap[++size] = element;
		int current = size;
		while(Heap[current]<Heap[parent(current)]){
			swap(current,parent(current));
			current = parent(current);
		}
	}
	
	public void print()
    {
        for (int i = 1; i <= size / 2; i++ )
        {
            System.out.print(" PARENT : " + Heap[i] + " LEFT CHILD : " + Heap[2*i] 
                + " RIGHT CHILD :" + Heap[2 * i  + 1]);
            System.out.println();
        } 
    }
	
	public int pop()//return the root of the heap which is the min element of the heap
	{
		int popped = Heap[FRONT];
		Heap[FRONT] = Heap[size--];
		minHeapify(FRONT);//after each pop operation, we need to re-adjust the heap
		return popped;
	}
	
	public void heapify()//public API to call heapify
	{
		minHeapify(FRONT);
	}
}